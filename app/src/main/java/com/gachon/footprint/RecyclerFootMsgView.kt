package com.gachon.footprint

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gachon.footprint.data.ModelFoot
import com.gachon.footprint.data.ModelReview
import com.gachon.footprint.reviewRecyclerView.ReviewRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_recycler_foot_msg_view.*
import java.util.*

class RecyclerFootMsgView : Activity() {

    var footMsgId: String? = null
    var db: FirebaseFirestore? = null
    var footmsgInfo: ModelFoot? = ModelFoot()
    var footrvInfo: ModelReview? = ModelReview()
    var curUser: ModelFoot? = ModelFoot()
    var reviewList = ArrayList<ModelReview>()
    var user = FirebaseAuth.getInstance().currentUser
    var document_id: String? = null
    private lateinit var reviewRecyclerAdapter: ReviewRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_foot_msg_view)
        db = FirebaseFirestore.getInstance()
        /*val mapbar = findViewById<Toolbar>(R.id.map_toolbar)
        actionBar(mapbar)
        setSupportActionBar(mapbar)
        val ab: androidx.appcompat.app.ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "내 주변 발자취"*/
        getUserInfo()
        getFootMsg()

        review_submit.setOnClickListener {
            addfirestore()
        }
    }


    /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.main_toolbar, menu)
         //맵 툴바를 가져옴
         return super.onCreateOptionsMenu(menu)
     }
 */

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edit_comment.windowToken, 0)
    }

    /*     override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item?.itemId) {
                //뒤로가기 버튼
                android.R.id.home -> {
                    finish()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }*/
    //해당 게시글 댓글 가져오기
    fun getReviewMsg() {

        db?.collection("Review")?.document(document_id.toString())?.collection("Comment")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                reviewList.clear()

                // 리사이클러뷰에 뿌려줌

                // 내가 작성한 글도 바로 바로 보여짐
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ModelReview::class.java)
                    //댓글 작성자의 닉네임과 프로필 사진을 최신화
                    db?.collection("User")?.document(item?.uid.toString())?.get()
                        ?.addOnSuccessListener { documentSnapshot ->
                            var user = documentSnapshot.toObject(ModelFoot::class.java)
                            item?.nickname = user?.nickname
                            item?.imageUrl = user?.imageUrl
                            reviewList.add(item!!)
                            // 어댑터 인스턴스 생성

                            reviewRecyclerAdapter = ReviewRecyclerAdapter()
                            reviewRecyclerAdapter.submitList(this.reviewList)
                            recyclerview_review.apply {
                                layoutManager =
                                    LinearLayoutManager(
                                        this@RecyclerFootMsgView,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                adapter = reviewRecyclerAdapter
                            }
                        }
                }
            }
    }

    //해당 게시글 정보 가져오기
    private fun getFootMsg() {
        footMsgId = intent.getStringExtra("FootMsgId")
        db?.collection("FootMsg")?.document(footMsgId.toString())?.get()
            ?.addOnSuccessListener { document ->
                footmsgInfo = document.toObject(ModelFoot::class.java)
                document_id = document.id
                getReviewMsg()
                setContent()
            }
    }

    //해당 게시글 정보 가져와서 뿌리기
    private fun setContent() {
        msg_title.text = footmsgInfo?.title
        msg_content.text = footmsgInfo?.msgText
        msg_nickname.text = footmsgInfo?.nickname
        Glide.with(this).load(footmsgInfo?.imageUrl).into(msg_img)

    }

    //댓글달면 파이어스토어에 추가
    private fun addfirestore() {
        footrvInfo?.reviewtxt = edit_comment.text.toString()
        footrvInfo?.uid = user?.uid
        footrvInfo?.nickname = curUser?.nickname
        footrvInfo?.let {
            db?.collection("Review")?.document(document_id.toString())?.collection("Comment")
                ?.add(footrvInfo!!)?.addOnSuccessListener {
                    edit_comment.setText("")
                    hideKeyboard()
                }
        }
    }

    //지금 사용자 유저 정보 가져옴
    private fun getUserInfo() {
        if (user != null) {
            db?.collection("User")?.document(user!!.uid)?.get()
                ?.addOnSuccessListener { documentSnapshot ->
                    curUser = documentSnapshot.toObject(ModelFoot::class.java)
                }
        }
    }
}


