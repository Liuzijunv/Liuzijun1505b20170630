package bwie.com.liuzijun1505b20170630;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * 类描述：
 * 创建人：Liuzijun
 * 创建时间：2017/6/30 15:16
 */
public class MyLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //传入路径,因为list为String格式,path为Object格式,所以强制类型转换.
        Glide.with(context).load((String) path).into(imageView);
    }
}