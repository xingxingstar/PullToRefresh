package com.example.zhuwojia.pulltorefresh;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import static android.view.View.GONE;

public class MainActivity extends BasicActivity {

    RecyclerView recyclerView;
    float firstDistance, secondDistance, distance;
    private RecyclerView.OnScrollListener onScrollListener;
    private int count = 10;
    private MyRecyclerViewAdapter adapter;
    private View refreshView, loadView;
    private ImageView pull_icon, refreshing_icon;
    private TextView tv_refresh_status, tv_load_status;
    private ProgressBar pb_loading;

    private final static int REFRESHING = 0;
    private final static int REFRESH_SUCCESS = -1;
    private final static int REFRESH_FAIL = -2;
    private final static int LOADING = 1;
    private final static int LOADING_SUCCESS = 2;
    private final static int LOADING_FAIL = 3;

    private Animation animation;
    private float refreshViewHeight, loadViewHeight, loadViewBottom;//上拉view高度，下拉view高度，下拉view的底部高度
    private float recyclerViewY, recyclerViewBottom;//上拉view高度，下拉view高度，下拉view的底部高度
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    setRefreshAnimation(REFRESH_FAIL);
                    break;
            }
        }
    };
    private boolean isFirst, isLast;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new MyRecyclerViewAdapter(this);
        adapter.setCount(count);
        recyclerView.setAdapter(adapter);
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                loadViewHeight = loadView.getHeight();
                loadViewBottom = loadView.getBottom();
                isLast = recyclerView.canScrollVertically(1);//表示是否能向上滚动，false表示已经滚动到底部
                isFirst = recyclerView.canScrollVertically(-1);//表示是否能向上滚动，false表示已经滚动到顶部+
                Logger.e("isFirst" + isFirst + ",isLast" + isLast);
//                recyclerViewY = recyclerView.getTop();
//                recyclerViewBottom = recyclerView.getBottom();
//                Log.e("====ssss", "top=" + recyclerViewY + ",bottom=" + recyclerViewBottom + ",ScreenHeight" + (getScreenHeigth() - getStatusHeight()));
                if (isFirst && isLast) {
                    return false;
                }
                float eventY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstDistance = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        distance = eventY - firstDistance;
                        if (distance > 0 && !isFirst) {//下拉操作
                            recyclerView.setY(distance);
                            refreshView.setVisibility(View.VISIBLE);
                            refreshViewHeight = refreshView.getHeight();
                            Logger.e("refreshHeight" + refreshViewHeight);
                            loadView.setVisibility(GONE);
                            refreshView.setY(distance - refreshViewHeight);
//                            超出刷新的view是箭头朝向反方向
                            if (distance > refreshViewHeight) {
                                pull_icon.setRotation(180f);
                                tv_refresh_status.setText(getString(R.string.release_to_refresh));
                            } else {
                                pull_icon.setImageResource(R.mipmap.ss_sxjt);
                                tv_refresh_status.setText(getString(R.string.pull_to_refresh));
                            }
                        } else if (distance < 0 && !isLast) {//上拉操作
                            recyclerView.setY(distance);
                            loadView.setVisibility(View.VISIBLE);
                            refreshView.setVisibility(View.GONE);
//                            此时distantce为负
                            loadView.setY((getScreenHeigth() - getStatusHeight()) + distance);
                        }
                    case MotionEvent.ACTION_UP:
                        if (distance > 0 && refreshViewHeight > 0 && !isFirst) {
//                            在下拉的高度超过刷新控件高度时进行刷新
                            if (distance > refreshViewHeight) {
                                refreshView.setY(0);
                                recyclerView.setY(refreshViewHeight);
                                setRefreshAnimation(REFRESHING);
                            } else {
                                refreshView.setVisibility(View.GONE);
                                loadView.setVisibility(View.GONE);
                                recyclerView.setY(refreshViewHeight);
                            }
                        } else if (loadViewHeight > 0) {
                            if (-distance > loadViewHeight && !isLast) {
                                recyclerView.measure(getScreenWidth(), getStatusHeight() - getStatusHeight());

//                            设置加载更多view位置，屏高-状态栏-view的高度
                                loadView.setY(getScreenHeigth() - getStatusHeight() - loadViewHeight);
                                setRefreshAnimation(LOADING);
                            } else if (-distance < loadViewHeight) {
                                loadView.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
                return MainActivity.super.onTouchEvent(event);
            }
        });


    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {

//        Logger.e("isFirst=" + isFirst + ",isLast=" + isLast);
//        if (!isFirst || !isLast) {
//            return true;
//        }
//        return false;
//    }


    private void initView() {
//        主布局view初始化
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        refreshView = findViewById(R.id.refreshView);
        loadView = findViewById(R.id.loadView);
//        下拉刷新view初始化
        pull_icon = (ImageView) findViewById(R.id.pull_icon);
        refreshing_icon = (ImageView) findViewById(R.id.refreshing_icon);
        tv_refresh_status = (TextView) findViewById(R.id.tv_refresh_status);
//        上拉加载更多view初始化
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tv_load_status = (TextView) findViewById(R.id.tv_load_status);

        //设置滑动监听，当滑动到最后一个时，加载更多
        if (onScrollListener == null) {
            onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    isLast = lastVisibleItemPosition == layoutManager.getItemCount() - 1;
                    if (isLast) {
                        Logger.e("scrollHeight" + dy);
                        //加载更多

                    }
                }
            };
            recyclerView.setOnScrollListener(onScrollListener);
        }
    }

    /**
     * 刷新视图的操作
     */
    private void setRefreshAnimation(int status) {
        switch (status) {
            case REFRESHING://刷新中
                animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                animation.setInterpolator(new LinearInterpolator());
                refreshing_icon.setAnimation(animation);
                tv_refresh_status.setText(getString(R.string.refreshing));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefresh(REFRESHING);
                    }
                }, 1000);
                break;
            case REFRESH_FAIL://刷新结束
                if (animation != null) {
                    refreshing_icon.clearAnimation();
                }
                pull_icon.setRotation(0f);
                tv_refresh_status.setText(getString(R.string.refresh_succeed));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.setVisibility(View.GONE);
                    }
                }, 1000);
                break;
            case LOADING:
                pb_loading.setVisibility(View.VISIBLE);
                tv_load_status.setText(getString(R.string.loading));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefresh(LOADING);
                        recyclerView.setBottom(getScreenHeigth() - getStatusHeight());
                    }
                }, 1000);
                break;
            case LOADING_SUCCESS:
                pb_loading.setVisibility(View.GONE);
                tv_load_status.setText(getString(R.string.load_succeed));
                loadView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 刷新执行的方法
     */
    private void setRefresh(int tag) {

        if (tag == REFRESHING) {
            count = 10;
            setRefreshAnimation(REFRESH_FAIL);
        } else {
            count += 10;
            setRefreshAnimation(LOADING_SUCCESS);
        }
        adapter.setCount(count);
        adapter.notifyDataSetChanged();
//        handler.sendEmptyMessageDelayed(tag, 1000);
    }


    private void setLoadMoreListener() {


    }


}
