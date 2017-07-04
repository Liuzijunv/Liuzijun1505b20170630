package bwie.com.liuzijun1505b20170630;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XListView;
/**
 * 类描述：
 * 创建人：Liuzijun
 * 创建时间：2017/6/30 15:16
 */
public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener {
    private Banner banner;
    private ArrayList<String> list_path;
    private XListView xListView;
    private List<News.ListBean> list;
    private Adapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = msg.obj.toString();
            Gson gson = new Gson();
            News news = gson.fromJson(result, News.class);
            list.addAll(news.getList());
            adapter.notifyDataSetChanged();
        }
    };
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xListView = (XListView) findViewById(R.id.xlistview);
        list = new ArrayList<>();
        //轮播图
        //initView();
        //获取网络请求
        into();
        //判断网络是否可用
        isNetworkAvailable(MainActivity.this);
        //跳转至开启网络页面
        dialog();
        //适配器
        adapter = new Adapter(MainActivity.this, list);
        xListView.setAdapter(adapter);

        xListView.setXListViewListener(this);
        xListView.setPullLoadEnable(true);
    }

    //刷新
    @Override
    public void onRefresh() {
        xListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2000);
    }

    //加载
    @Override
    public void onLoadMore() {
        xListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                into();
                onLoad();
            }
        }, 2000);
    }

    private void onLoad() {
        xListView.stopRefresh();//停止刷新
        xListView.stopLoadMore();//停止加载更多
        SimpleDateFormat formatter = new SimpleDateFormat("MM-ddHH:mm:ss");//设置日期显示格式
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);// 将时间装换为设置好的格式
        xListView.setRefreshTime(str);//设置时间
    }

    //获取网络请求
    private void into() {
        new Thread() {
            @Override
            public void run() {
                String result = Utils.getUrlConnect("http://qhb.2dyt.com/Bwei/news?page=1&type=6&postkey=1503d");
                if (result != null) {
                    Message message = Message.obtain();
                    message.obj = result;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    private void initView() {
        banner = (Banner) findViewById(R.id.banner);
        list_path = new ArrayList<>();
        list_path.add("http://img.taopic.com/uploads/allimg/101228/290-10122P9533213.jpg");
        list_path.add("http://pic.58pic.com/58pic/12/95/68/62V58PICMT4.jpg");
        list_path.add("http://imgsrc.baidu.com/image/c0%3Dshijue%2C0%2C0%2C245%2C40/sign=4ea99ec2f8d3572c72ef949fe27a0952/f7246b600c338744af80e6575b0fd9f9d72aa050.jpg");
        //设置banner属性
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new MyLoader());
        //设置图片集合
        banner.setImages(list_path);
        //banner设置方法全部调用完毕时最后调用
        banner.start();


    }

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    Toast.makeText(context, "当前有可用网络！", Toast.LENGTH_LONG).show();
                    return true;
                }
            } else {
                Toast.makeText(context, "当前没有可用网络！", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private void dialog() {

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("继续", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intentSettings;
                if(android.os.Build.VERSION.SDK_INT > 10){//判断版本(3.0以上)
                    intentSettings = new Intent(Settings.ACTION_SETTINGS);
                }else{
                    intentSettings = new Intent();
                    intentSettings.setClassName("com.android.phone","com.android.phone.MobileNetWorkSettings");
                }
                startActivity(intentSettings);
            }
        });
        builder.create().show();
    }


    /**
     * 类描述：
     * 创建人：yekh
     * 创建时间：2017/5/10 10:52
     */
    class Adapter extends BaseAdapter {
        private static final int TYPE_1 = 0;
        private static final int TYPE_2 = 1;
        private Context context;
        private List<News.ListBean> list;
        private ImageLoader ImageLoader;

        public Adapter(Context context, List<News.ListBean> list) {
            this.context = context;
            this.list = list;
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
            ImageLoader = ImageLoader.getInstance();
            ImageLoader.init(configuration);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 得到你想要显示的布局类型
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {

            if (position % 2 == 0) {
                return TYPE_1;
            } else {
                return TYPE_2;
            }
        }

        /**
         * 指定你要加载的条目类型
         *
         * @return
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            ViewHolder2 viewHolder2 = null;
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_1: {
                    if (convertView == null) {
                        viewHolder = new ViewHolder();
                        convertView = convertView.inflate(context, R.layout.item1, null);
                        viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.image1);
                        viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.image2);
                        viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.image3);
                        viewHolder.textView1 = (TextView) convertView.findViewById(R.id.text1);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) convertView.getTag();
                    }
                    viewHolder.imageView1.setImageResource(R.mipmap.ic_launcher);
                    ImageLoader.displayImage(list.get(position).getPic(), viewHolder.imageView1);
                    ImageLoader.displayImage(list.get(position).getPic(), viewHolder.imageView2);
                    ImageLoader.displayImage(list.get(position).getPic(), viewHolder.imageView3);
                    viewHolder.textView1.setText(list.get(position).getTitle());
                }
                break;
                case TYPE_2: {
                    if (convertView == null) {
                        viewHolder2 = new ViewHolder2();
                        convertView = convertView.inflate(context, R.layout.item2, null);
                        viewHolder2.imageView4 = (ImageView) convertView.findViewById(R.id.image4);
                        viewHolder2.textView2 = (TextView) convertView.findViewById(R.id.text2);
                        convertView.setTag(viewHolder2);
                    } else {
                        viewHolder2 = (ViewHolder2) convertView.getTag();
                    }
                    viewHolder2.imageView4.setImageResource(R.mipmap.ic_launcher);
                    ImageLoader.displayImage(list.get(position).getPic(), viewHolder2.imageView4);
                    viewHolder2.textView2.setText(list.get(position).getTitle());
                }
                break;
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView1, imageView2, imageView3;
            TextView textView1;

        }

        class ViewHolder2 {
            ImageView imageView4;
            TextView textView2;
        }
    }
}

