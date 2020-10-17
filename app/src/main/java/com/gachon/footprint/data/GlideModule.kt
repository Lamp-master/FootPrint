/*
package com.gachon.footprint.data

import android.content.Context
import android.provider.ContactsContract
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream

@GlideModule
open class AppGlideModule : AppGlideModule() {
    @Override
    public fun registerComponents(context : Context, glide : Glide, registry: Registry){
        super.registerComponents(context, glide, registry)
        registry.append(StorageReference::class.java, InputStream::class.java, FirebaseImageLoader.Factory())
    }
}*/
