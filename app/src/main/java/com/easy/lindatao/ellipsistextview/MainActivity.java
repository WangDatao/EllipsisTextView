package com.easy.lindatao.ellipsistextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.easy.lindatao.ellipsistextview.widget.EllipsisTextView;

public class MainActivity extends AppCompatActivity
{

    private EllipsisTextView ell;
    private ListView lv;
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setData();
    }

    private void initView()
    {
        ell = (EllipsisTextView) findViewById(R.id.ellipsis_view);
        lv = (ListView) findViewById(R.id.list_view);
    }

    private void setData()
    {
        mText = "因此，为争夺地盘、食物，经查发生斗殴事件。想要在这种残酷的环境活下来，就得拥有强壮的体魄。";

        ell.setText(mText);
        ell.append(R.mipmap.label);
        ell.append(R.mipmap.label);

        lv.setAdapter(new MyAdapter());
    }



   private class MyAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int i)
        {
            return null;
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = View.inflate(MainActivity.this , R.layout.list_item , null);
                holder.ellView = (EllipsisTextView) convertView.findViewById(R.id.list_ell);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ellView.setText(mText);
            holder.ellView.clearDrawable();
            if(position == 0)
            {
                holder.ellView.append(R.mipmap.label);
            }
            else if(position ==1)
            {
                holder.ellView.append(R.mipmap.label);

                holder.ellView.append(R.mipmap.label);
            }
            else if(position ==2)
            {
                holder.ellView.append(R.mipmap.label);

                holder.ellView.append(R.mipmap.label);
                holder.ellView.append(R.mipmap.label);

            }
            else if (position == 3)
            {
                holder.ellView.append(R.mipmap.label);
                holder.ellView.append(R.mipmap.label);
                holder.ellView.append(R.mipmap.label);
                holder.ellView.append(R.mipmap.label);
            }

            return convertView;
        }
    }

    private static class ViewHolder{
        EllipsisTextView ellView;
    }

}
