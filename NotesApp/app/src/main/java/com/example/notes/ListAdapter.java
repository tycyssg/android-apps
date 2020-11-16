package com.example.notes;

//public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
//
//    Context context;
//    List<Note> noteList;
//
//    public ListAdapter(Context context, List<Note> noteList) {
//        this.context = context;
//        this.noteList = noteList;
//    }
//
//    @NonNull
//    @Override
//    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ListViewHolder(LayoutInflater.from(context).inflate(R.layout.list_row,parent,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
//            holder.noteTitle.setText(noteList.get(position).getNoteTitle());
//            holder.noteDate.setText(noteList.get(position).getDateCreated().toString());
//            holder.noteBody.setText(noteList.get(position).getNoteBody());
//    }
//
//    @Override
//    public int getItemCount() {
//        return noteList.size();
//    }
//

//    class ListViewHolder extends RecyclerView.ViewHolder{
//        TextView noteTitle,noteDate,noteBody;
//        public ListViewHolder(@NonNull View itemView) {
//            super(itemView);
//            noteTitle = itemView.findViewById(R.id.noteTitleText);
//            noteDate = itemView.findViewById(R.id.noteDateText);
//            noteBody = itemView.findViewById(R.id.noteBodyText);
//        }
//    }

//}
