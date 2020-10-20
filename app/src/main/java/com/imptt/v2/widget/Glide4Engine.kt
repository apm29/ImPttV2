package com.imptt.v2.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.imptt.v2.di.GlideApp
import com.zhihu.matisse.engine.ImageEngine

/**
 *  author : ciih
 *  date : 2020/10/20 5:06 PM
 *  description :
 */


/**
 *
 * Created by Administrator on 2017/12/25.
 * Matisse图片引擎
 */
class Glide4Engine : ImageEngine {
    override fun loadThumbnail(
        context: Context,
        resize: Int,
        placeholder: Drawable,
        imageView: ImageView,
        uri: Uri
    ) {
        GlideApp.with(context)
            .asBitmap() // some .jpeg files are actually gif
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .override(resize, resize)
                    .centerCrop()
            )
            .into(imageView)
    }

    /**
     * Load thumbnail of a gif image resource. You don't have to load an animated gif when it's only
     * a thumbnail tile.
     *
     * @param context     Context
     * @param resize      Desired size of the origin image
     * @param placeholder Placeholder drawable when image is not loaded yet
     * @param imageView   ImageView widget
     * @param uri         Uri of the loaded image
     */
    override fun loadGifThumbnail(
        context: Context?,
        resize: Int,
        placeholder: Drawable?,
        imageView: ImageView?,
        uri: Uri?
    ) {
        GlideApp.with(context!!)
            .asBitmap()
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(placeholder)
                    .override(resize, resize)
                    .centerCrop()
            )
            .into(imageView!!)
    }


    override fun loadImage(
        context: Context,
        resizeX: Int,
        resizeY: Int,
        imageView: ImageView,
        uri: Uri
    ) {
        GlideApp.with(context)
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resizeX, resizeY)
                    .priority(Priority.HIGH)
            )
            .into(imageView)
    }

    /**
     * Load a gif image resource.
     *
     * @param context   Context
     * @param resizeX   Desired x-size of the origin image
     * @param resizeY   Desired y-size of the origin image
     * @param imageView ImageView widget
     * @param uri       Uri of the loaded image
     */
    override fun loadGifImage(
        context: Context?,
        resizeX: Int,
        resizeY: Int,
        imageView: ImageView?,
        uri: Uri?
    ) {
        GlideApp.with(context!!)
            .asGif()
            .load(uri)
            .apply(
                RequestOptions()
                    .override(resizeX, resizeY)
                    .priority(Priority.HIGH)
            )
            .into(imageView!!)
    }



    override fun supportAnimatedGif(): Boolean {
        return true
    }
}