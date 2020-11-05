package com.imptt.v2.view.adapter.helper

import androidx.recyclerview.widget.DiffUtil
import com.kylindev.pttlib.db.ChatMessageBean

/**
 *  author : ciih
 *  date : 2020/11/5 2:33 PM
 *  description :
 */
class MessageDiffer(
    private val oldItems:List<ChatMessageBean>,
    private val newItems:List<ChatMessageBean>
) :DiffUtil.Callback(){
    /**
     * Returns the size of the old list.
     *
     * @return The size of the old list.
     */
    override fun getOldListSize(): Int {
        return oldItems.size
    }

    /**
     * Returns the size of the new list.
     *
     * @return The size of the new list.
     */
    override fun getNewListSize(): Int {
        return newItems.size
    }

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     *
     *
     * For example, if your items have unique ids, this method should check their id equality.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].cid == newItems[newItemPosition].cid
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * DiffUtil uses this information to detect if the contents of an item has changed.
     *
     *
     * DiffUtil uses this method to check equality instead of [Object.equals]
     * so that you can change its behavior depending on your UI.
     * For example, if you are using DiffUtil with a
     * [RecyclerView.Adapter], you should
     * return whether the items' visual representations are the same.
     *
     *
     * This method is called only if [.areItemsTheSame] returns
     * `true` for these items.
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     * oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return false
    }
}