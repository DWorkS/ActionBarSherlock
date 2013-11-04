package dev.dworks.libs.actionbarplus;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.R;

import dev.dworks.libs.widget.FillerView;
import dev.dworks.libs.widget.HeaderView;
import dev.dworks.libs.widget.PinnedSectionGridView.PinnedSectionGridAdapter;

public class SimpleSectionedGridAdapter extends BaseAdapter implements PinnedSectionGridAdapter{
	protected static final int TYPE_FILLER = 0;
	protected static final int TYPE_HEADER = 1;
	protected static final int TYPE_HEADER_FILLER = 2;
    private boolean mValid = true;
    private int mSectionResourceId;
    private LayoutInflater mLayoutInflater;
    private ListAdapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
	private int mColumns;
	private int mWidth;
	private Context mContext;
//	private View mLastHeaderViewSeen;
	private View mLastViewSeen;
	private GridView mGridView;

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;
        int type = 0;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }

    public SimpleSectionedGridAdapter(Context context, int sectionResourceId, BaseAdapter baseAdapter) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;
        mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mValid = !mBaseAdapter.isEmpty();
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                mValid = false;
                notifyDataSetInvalidated();
            }
        });
    }
    
    public void setGridView(GridView gridView){
    	mGridView = gridView;
        mColumns = gridView.getNumColumns();
    	mWidth = gridView.getWidth();
    }

    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (int i = 0; i < sections.length; i++) {
			Section section = sections[i];
    		Section sectionAdd;
 
        	for (int j = 0; j < mColumns - 1; j++) {
        		sectionAdd = new Section(section.firstPosition, section.title);
        		sectionAdd.type = TYPE_HEADER_FILLER;
        		sectionAdd.sectionedPosition = sectionAdd.firstPosition + offset;
                mSections.append(sectionAdd.sectionedPosition, sectionAdd);
                ++offset;
			}
    		
    		sectionAdd = new Section(section.firstPosition, section.title);
    		sectionAdd.type = TYPE_HEADER;
    		sectionAdd.sectionedPosition = sectionAdd.firstPosition + offset;
            mSections.append(sectionAdd.sectionedPosition, sectionAdd);
            ++offset;
        	
            if(i+1 < sections.length){
            	int nextPos = sections[i+1].firstPosition;
            	int itemsCount = nextPos - section.firstPosition;
            	int dummyCount = mColumns - (itemsCount % mColumns);
            	if(mColumns != dummyCount){
	            	for (int j = 0 ;j < dummyCount; j++) {
	                	sectionAdd = new Section(section.firstPosition, section.title);
	            		sectionAdd.type = TYPE_FILLER;
	            		sectionAdd.sectionedPosition = nextPos + offset;
	            		mSections.append(sectionAdd.sectionedPosition, sectionAdd);
	            		++offset;
					}
            	}
            }
		}

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return ListView.INVALID_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    @Override
    public int getCount() {
        return (mValid ? mBaseAdapter.getCount() + mSections.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return isSectionHeaderPosition(position)
                ? mSections.get(position)
                : mBaseAdapter.getItem(sectionedPositionToPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? getViewTypeCount() - 1
                : mBaseAdapter.getItemViewType(position);
    }

    @Override
    public boolean isEnabled(int position) {
        //noinspection SimplifiableConditionalExpression
        return isSectionHeaderPosition(position)
                ? false
                : mBaseAdapter.isEnabled(sectionedPositionToPosition(position));
    }

    @Override
    public int getViewTypeCount() {
        return mBaseAdapter.getViewTypeCount() + 1; // the section headings
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return mBaseAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mBaseAdapter.isEmpty();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	int i = 0;
        if (isSectionHeaderPosition(position)) {
        	HeaderView header;
        	TextView view;
        	if(null == convertView){
        		convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
        	}
        	else{
        		if(null == convertView.findViewById(R.id.header_layout)){
        			convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);	
        		}
        	}
			switch (mSections.get(position).type) {
			case TYPE_HEADER:
				header = (HeaderView) convertView.findViewById(R.id.header_layout);
				view = (TextView) convertView.findViewById(R.id.header);
	            view.setText(mSections.get(position).title);
	            header.setHeaderWidth(mWidth);
	            header.setView(view);
	            header.forceLayout();
	            //view.setBackgroundColor(Color.BLUE);
	            break;
			case TYPE_HEADER_FILLER:
				header = (HeaderView) convertView.findViewById(R.id.header_layout);
				view = (TextView) convertView.findViewById(R.id.header);
	            view.setText("");
	            header.setHeaderWidth(0);
	            header.setView(view);
	            header.forceLayout();
				break;
			default:
				convertView = getFillerView(convertView, parent, mLastViewSeen);
				break;
			}
        } else {
            convertView = mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
        	mLastViewSeen = convertView; 
        }
        return convertView;
    }
    
    private FillerView getFillerView(View convertView, ViewGroup parent, View lastViewSeen) {
        FillerView fillerView = null;
        if (fillerView == null) {
            fillerView = new FillerView(mContext);
        }
        fillerView.setMeasureTarget(lastViewSeen);
        return fillerView;
    }

	@Override
	public boolean isItemViewTypePinned(int position) {
		Section section = mSections.get(position); 
		return isSectionHeaderPosition(position) && section.type == TYPE_HEADER;
	}

	public static class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
}