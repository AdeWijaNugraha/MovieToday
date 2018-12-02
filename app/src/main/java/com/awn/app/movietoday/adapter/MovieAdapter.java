package com.awn.app.movietoday.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.awn.app.movietoday.DetailActivity;
import com.awn.app.movietoday.R;
import com.awn.app.movietoday.item.MovieItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<MovieItem> listMovie;

    private Context context;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<MovieItem> getListMovie() {
        return listMovie;
    }

    public void setListMovie(ArrayList<MovieItem> listMovie) {
        this.listMovie = listMovie;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        bind item to recycler
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(getListMovie().get(position));
    }

    @Override
    public int getItemCount() {
        return getListMovie().size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

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

        public MovieViewHolder(View itemView) {
            super(itemView);

//            binding view
            ButterKnife.bind(this, itemView);
        }

        public void bind(final MovieItem item) {
//            set title
            tv_title.setText(item.getTitle());

//            set rating
            double userRating = Double.parseDouble(item.getRating()) / 2;
            int integerPart = (int) Math.floor(userRating);
//            Fill full stars
            for (int i = 0; i < integerPart; i++) {
                img_vote.get(i).setImageResource(R.drawable.ic_star_full);
            }
//            Fill half star
            if (Math.round(userRating) > integerPart) {
                img_vote.get(integerPart).setImageResource(R.drawable.ic_star_half);
            }

//            set overview
            tv_overview.setText(item.getOverview());

//            set poster
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

//                    move to detail activity
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.KEY_MOVIE, item);
                    itemView.getContext().startActivity(intent);
                }
            });

            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    activate intent for share information
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "MovieToday");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Overview of " + item.getTitle());
                    intent.putExtra(Intent.EXTRA_TEXT, "Yeay! Today is a great day! \nLet's join with us to watch \""
                            + item.getTitle()
                            + "\" ! \n\nThis film tells about "
                            + item.getOverview()
                            + "\n\nWaitt! Check the schedule on your cinema and don't forget to buy some popcorn and soft drink! :D");
                    itemView.getContext().startActivity(Intent.createChooser(intent, itemView.getResources().getString(R.string.share)));
                }
            });
        }
    }
}