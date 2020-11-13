package com.imptt.v2.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.imptt.v2.R
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val data = arrayListOf<Int>()
        for (i in 1..20){
            data.add(i)
        }
        list.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,true)
        list.adapter = object :RecyclerView.Adapter<VH>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

                return VH(
                    layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                )
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.itemView.findViewById<TextView>(android.R.id.text1).text = data[position].toString()
            }

            override fun getItemCount(): Int {
                return data.size
            }

        }
        list.itemAnimator = DefaultItemAnimator()
//        refresh.setOnRefreshListener {
//            val position =
//                getRecyclerViewLastPosition(list.layoutManager as LinearLayoutManager, list, data)
//            for (i in 1..20){
//                data.add(0, i)
//            }
//            (list.adapter)?.notifyItemRangeChanged(0, 20)
//            refresh.isRefreshing = false
//            list.scrollToPosition(position[0])
//        }
        button.setOnClickListener {
            val position = (list.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            for (i in 1..20){
                data.add(0, i)
            }
            (list.adapter)?.notifyItemRangeChanged(0, 20)
//            list.scrollToPosition(position)
        }

    }

    class VH(itemView: View):RecyclerView.ViewHolder(itemView)

    //获得RecyclerView滑动的位置
    private fun getRecyclerViewLastPosition(layoutManager: LinearLayoutManager,recyclerView: RecyclerView,data:List<*>): IntArray {
        val pos = IntArray(2)
        pos[0] = layoutManager.findFirstCompletelyVisibleItemPosition()
        val orientationHelper =
            OrientationHelper.createOrientationHelper(layoutManager, RecyclerView.VERTICAL)
        val fromIndex = 0
        val toIndex: Int = data.size
        val start = orientationHelper.startAfterPadding
        val end = orientationHelper.endAfterPadding
        val next = if (toIndex > fromIndex) 1 else -1
        var i = fromIndex
        while (i != toIndex) {
            val child: View = recyclerView.getChildAt(i)
            val childStart = orientationHelper.getDecoratedStart(child)
            val childEnd = orientationHelper.getDecoratedEnd(child)
            if (childStart < end && childEnd > start) {
                if (childStart >= start && childEnd <= end) {
                    pos[1] = childStart
                    return pos
                }
            }
            i += next
        }
        return pos
    }
}