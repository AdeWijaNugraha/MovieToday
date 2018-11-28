package com.awn.app.movietoday.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.awn.app.movietoday.DetailActivity;
import com.awn.app.movietoday.R;
import com.awn.app.movietoday.items.MovieItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Cursor cur;

    public FavoriteAdapter(Cursor items) {
        replaceAll(items);
    }

    public void replaceAll(Cursor items) {
        cur = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        if (cur == null) return 0;
        return cur.getCount();
    }

    private MovieItem getItem(int position) {
        if (!cur.moveToPosition(position)) {
            throw new IllegalStateException("Illegal State");
        }
        return new MovieItem(cur);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_poster)
        ImageView iv_poster;

        @BindView(R.id.tv_item_title)
        TextView tv_title;

        @BindViews({
                R.id.img_star1,
                R.id.img_star2,
                R.id.img_star3,
                R.id.img_star4,
                R.id.img_star5
        })
        List<ImageView> img_vote;

        @BindView(R.id.tv_item_overview)
        TextView tv_overview;

        @BindView(R.id.btn_item_detail)
        Button btn_detail;

        @BindView(R.id.btn_item_share)
        Button btn_share;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final MovieItem item) {
            tv_title.setText(item.getTitle());

            double userRating = Double.parseDouble(item.getRating()) / 2;
            int integerPart = (int) userRating;
            // Fill stars
            for (int i = 0; i < integerPart; i++) {
                img_vote.get(i).setImageResource(R.drawable.ic_star_full);
            }
            // Fill half star
            if (Math.round(userRating) > integerPart) {
                img_vote.get(integerPart).setImageResource(R.drawable.ic_star_half);
            }

            tv_overview.setText(item.getOverview());

            Glide.with(itemView.getContext())
                    .load("http://image.tmdb.org/t/p/w185" + item.getPoster())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .centerCrop()
                    )
                    .into(iv_poster);

            btn_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.KEY_MOVIE, item);
                    itemView.getContext().startActivity(intent);
                }
            });

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "MovieToday");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Overview of " + item.getTitle());
                    intent.putExtra(Intent.EXTRA_TEXT, item.getOverview());
                    itemView.getContext().startActivity(Intent.createChooser(intent, itemView.getResources().getString(R.string.share)));
                }
            });
        }
    }
}
