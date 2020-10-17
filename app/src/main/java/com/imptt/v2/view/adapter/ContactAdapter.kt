package com.imptt.v2.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.imptt.v2.R
import com.imptt.v2.data.model.Contact

/**
 *  author : ciih
 *  date : 2020/10/17 10:24 AM
 *  description :
 */
class ContactAdapter(
    private val contact: Contact,
    private val layoutInflater: LayoutInflater
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return contact.groups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return contact.items[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return contact.groups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return contact.items[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return contact.items[groupPosition][childPosition].hashCode().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val itemView = convertView ?: layoutInflater.inflate(
            R.layout.contact_group_layout,
            parent,
            false
        )
        val group: String = contact.groups[groupPosition]
        val count: Int = contact.items[groupPosition].size
        val tvGroup = itemView.findViewById(R.id.textViewGroupName) as TextView
        val tvGroupCount = itemView.findViewById(R.id.textViewGroupContactCount) as TextView
        tvGroup.text = group
        tvGroupCount.text = "${count}人"
        return itemView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val itemView = convertView ?: layoutInflater.inflate(
            R.layout.contact_item_layout,
            parent,
            false
        )
        val child: String = contact.items[groupPosition][childPosition]
        val textViewContactName = itemView.findViewById(R.id.textViewContactName) as TextView
        val textViewContactSubtitle =
            itemView.findViewById(R.id.textViewContactSubtitle) as TextView
        textViewContactName.text = child
        textViewContactSubtitle.text = "在线"
        return itemView
    }

    override fun isChildSelectable(
        groupPosition: Int,
        childPosition: Int
    ): Boolean {
        return true
    }

}