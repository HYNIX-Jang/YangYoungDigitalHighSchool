package hynixlabs.com.yydigital.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import hynixlabs.com.yydigital.R;

public class NoticeHolder extends RecyclerView.ViewHolder {
    public TextView txtNoticeInTitle;
    public TextView txtNoticeInView;
    public TextView txtNoticeInAuthor;
    public TextView txtNoticeInDate;
    public CardView cardView;

    public NoticeHolder(View view) {
        super(view);
        txtNoticeInTitle = view.findViewById(R.id.txtNoticeInTitle);
        txtNoticeInView = view.findViewById(R.id.txtNoticeInView);
        txtNoticeInAuthor = view.findViewById(R.id.txtNoticeInAuthor);
        txtNoticeInDate = view.findViewById(R.id.txtNoticeInDate);
        cardView = view.findViewById(R.id.noticeInCardView);

    }

}
