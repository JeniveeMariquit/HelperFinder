package mobapp.dntsjay.helperfinder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {
    List<clsMessageListData> listdata;

    public RecyclerViewAdapter(List<clsMessageListData> listdata) {
        this.listdata = listdata;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_info,viewGroup,false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        clsMessageListData data = listdata.get(position);
        holder.HsentByName.setText(data.getSentByName());
        holder.Hcontent.setText(data.getContent());
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView HsentByName , Hcontent;

        public MyHolder(View itemView) {
            super(itemView);
            HsentByName = (TextView) itemView.findViewById(R.id.txtSentByName);
            Hcontent = (TextView) itemView.findViewById(R.id.txtMessageContent);

        }
    }
}
