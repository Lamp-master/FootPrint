package com.gachon.footprint

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.footprint_dialog.*


//다이어리 액티비티 아니고 임시로 창 열어보려고 사용하였음
class DiaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        imsibutton.setOnClickListener() {
            val dialog = FootDialog.FootDialogBuild()

                .setDialogTitle("발자취 보기")
                .setTitle("타이틀")
                .setLocation("이것은 위치")
                .setContent("이것은 내용인데 과연 어디까지 내용이 찰지 좀 궁금하긴하니까 일단 길게는 채워봄 에러 안났으면 좋겠다 솔직히 날거알지만 안나면 너무좋을꺼같다 좀더치면 스크롤도 나오겠지 일단 아무말을 하자 음 오늘은 뭐먹지 맛있는거 먹고싶은데 흠 치킨 좀더뭔가좀더써봄 스크롤생겨야함")
                .setNickname("닉네임")
                .setRecommend("추천")
                .setComment("댓글")

                .create()
            dialog.show(supportFragmentManager, dialog.tag)

        }
    }
}