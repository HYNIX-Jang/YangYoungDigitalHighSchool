package hynixlabs.com.yydigital.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hynixlabs.com.yydigital.R;
import hynixlabs.com.yydigital.holder.NoticeHolder;
import hynixlabs.com.yydigital.vo.NoticeItem;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeHolder> {

    private Context context;
    private List<NoticeItem> noticeItem;
    private NoticeItem vo;
    private TextView txtNoticeInTitle;
    private TextView txtNoticeInView;
    private TextView txtNoticeInAuthor;
    private TextView txtNoticeInDate;
    private CardView cardView;


    public NoticeAdapter(Context context, List<NoticeItem> noticeItem) {
        this.context = context;
        this.noticeItem = noticeItem;
    }

    @NonNull
    @Override // 새로운 뷰 홀더 생성
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_cardview, parent,
                false);
        return new NoticeHolder(view); //Holder로 View 넘김
    }

    @Override  // View 의 내용을 해당 포지션의 데이터로 바꿈(바인딩)
    public void onBindViewHolder(@NonNull NoticeHolder holder, final int position) {
        txtNoticeInTitle = holder.txtNoticeInTitle;
        txtNoticeInView = holder.txtNoticeInView;
        txtNoticeInAuthor = holder.txtNoticeInAuthor;
        txtNoticeInDate = holder.txtNoticeInDate;
        cardView = holder.cardView;

        txtNoticeInTitle.setText(noticeItem.get(position).getTitle());
        txtNoticeInView.setText(noticeItem.get(position).getView());
        txtNoticeInAuthor.setText(noticeItem.get(position).getAuthor());
        txtNoticeInDate.setText(noticeItem.get(position).getDate());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = noticeItem.get(position).getURL();
                System.out.println(URL);
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeItem.size();
    }


}
