package com.pdmanager.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pdmanager.core.R;
import com.pdmanager.core.models.Patient;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.ListViewHolder;
import com.telerik.widget.list.RadListView;
import com.telerik.widget.list.SlideLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by George on 6/4/2016.
 */
public class PatientChartFragment extends BasePDFragment implements IBasePatientChartFragment {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Patient patient;
    private Button mButtonPatients;
    private RadListView listView;
    private SlideLayoutManager slideLayoutManager;
    private DestinationsAdapter adapter;
    private View rootView;
    private LruCache<String, Bitmap> mMemoryCache;

    public Patient getPatient() {
        return patient;

    }

    public void setPatient(Patient p) {

        patient = p;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView != null) {
            return rootView;
        }
        if (savedInstanceState != null) {

            patient = savedInstanceState.getParcelable("Patient");

        }
        rootView = inflater.inflate(R.layout.fragment_patientchart, container, false);
        listView = (RadListView) rootView.findViewById(R.id.listView);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        //listView.getLayoutManager().se.setItemSpacing(20);
        listView.setLayoutManager(layoutManager);
        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }


        //slideLayoutManager = new SlideLayoutManager(getActivity());

        adapter = new DestinationsAdapter(getData());
        //updateListViewLayoutParams();
        listView.setAdapter(adapter);

        listView.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int itemPosition, MotionEvent motionEvent) {
//                if (itemPosition == slideLayoutManager.getCurrentPosition()) {
                navigateToDetailsFragment(itemPosition);
                //              }
            }

            @Override
            public void onItemLongClick(int itemPosition, MotionEvent motionEvent) {

            }
        });

//        listView.setLayoutManager(slideLayoutManager);

        //  if (savedInstanceState != null) {
        ///      int currentPosition = savedInstanceState.getInt("currentPosition", 0);
        //      slideLayoutManager.scrollToPosition(currentPosition);
        //   }


        return rootView;
    }

    private void performClick(int position) {


        navigateToDetailsFragment(position);
        /*
        if(position > slideLayoutManager.getCurrentPosition()) {
            slideLayoutManager.scrollToNext();
        } else if(position < slideLayoutManager.getCurrentPosition()) {
            slideLayoutManager.scrollToPrevious();
        } else {

        }
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

        /*if(slideLayoutManager != null) {
            int currentPosition = slideLayoutManager.getCurrentPosition();
            outState.putInt("currentPosition", currentPosition);
        }
        */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        RetainFragment retainFragment =
                RetainFragment.findOrCreateRetainFragment(getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
            retainFragment.mRetainedCache = mMemoryCache;
        }
        */
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void navigateToDetailsFragment(int item) {

        if (item == 0) {
            ClinicalInfoFragment newFragment = new ClinicalInfoFragment();
            newFragment.setPatient(patient);
            //  ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

        } else if (item == 1) {

            TestAssessmentFragment newfragment = new TestAssessmentFragment();
            newfragment.setPatient(patient);
            //  ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newfragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

            /*
            Intent mainIntent = new Intent(getActivity(), BS11Activity.class);
            mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_CODE,patient.Id);
            //mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_NAME, p.Given + " " + p.Family);
            getActivity().startActivity(mainIntent);

            */
            /*PatientAllergyFragment newFragment = new PatientAllergyFragment();
            newFragment.setPatient(patient);
            //  ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
            */

        } else if (item == 2) {
            MedicationOrderFragment newFragment = new MedicationOrderFragment();
            newFragment.setPatient(patient);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

        } else if (item == 3) {
            AssessmentFragment newFragment = new AssessmentFragment();
            newFragment.setPatient(patient);
            //  ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

        } else if (item == 4) {
            PatientCalendarFragment newFragment = new PatientCalendarFragment();
            newFragment.setPatient(patient);
            //  ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

        } else if (item == 5) {
            ObservationChartFragment newFragment = new ObservationChartFragment();
            newFragment.setPatient(patient);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();

        } else

        {
            Fragment newFragment = new PatientChartItemFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            ((OnDestinationSelectedListener) newFragment).onDestinationSelected((Destination) adapter.getItem(item));
            transaction.replace(R.id.container, newFragment);

            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
        }
    }

  /*  public void loadBitmap(int resId, ImageView imageView, int reqWidth, int reqHeight) {

        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //imageView.setImageResource(0);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, reqWidth, reqHeight);
            task.execute(resId);
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    */

    private List getData() {
        ArrayList<Destination> destinations = new ArrayList<>();

        Destination clinicalInformation = new Destination("Clinical Information", "Check the clinical information of the patient", R.drawable.clinicalinfo, Color.parseColor("#555555"));
        destinations.add(clinicalInformation);

        //Destination allergy = new Destination("Allergies", "Attention. Patient has allergy in garlic!", R.drawable.allergies, Color.parseColor("#D06744"));
        //destinations.add(allergy);

        Destination allergy = new Destination("Tests", "View past test scores and create a new test for the patient", R.drawable.allergies, Color.parseColor("#D06744"));
        destinations.add(allergy);

        Destination medication = new Destination("Medication", "Check medication schedule and patient medication adherence", R.drawable.medication, Color.parseColor("#D06744"));
        destinations.add(medication);

        Destination communication = new Destination("Assessment", "Check the overall patient assessment", R.drawable.communication, Color.parseColor("#018CB0"));
        destinations.add(communication);

        Destination calendar = new Destination("Calendar", "Check patient activity per day ", R.drawable.communication, Color.parseColor("#D19B33"));

        destinations.add(calendar);


        Destination motorSymptoms = new Destination("Motor symptoms", "Check patient motor symptoms ", R.drawable.communication, Color.parseColor("#D06744"));

        destinations.add(motorSymptoms);
        return destinations;
    }

    public interface OnDestinationSelectedListener {
        void onDestinationSelected(Destination destination);
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragment";
        public LruCache<String, Bitmap> mRetainedCache;

        public RetainFragment() {
        }

        static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

    public class DestinationsAdapter extends ListViewAdapter {
        private int width;
        private int height;

        public DestinationsAdapter(List items) {
            super(items);
        }

        public void setDimens(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.list_view_patientchart_layout, parent, false);
            return new DestinationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            Destination item = (Destination) this.getItem(position);

            final DestinationViewHolder typedVh = (DestinationViewHolder) holder;

            // loadBitmap(item.src, typedVh.destinationImage, width, height);
            typedVh.position = position;
            typedVh.separator.setBackgroundColor(item.color);
            typedVh.destinationTitle.setText(item.title);
            typedVh.destinationTitle.setTextColor(item.color);
            typedVh.destinationInfo.setText(item.info);
            //typedVh.destinationEnquiryLayout.setBackgroundColor(item.color);
            typedVh.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick(typedVh.position);
                }
            });
        }
    }

    /*private void updateListViewLayoutParams() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            height -= actionBarHeight;
        }
        int offset;
        int cardHeight;
        int cardWidth;
        int maxWidth = 1067;
        int maxHeight = 5 * maxWidth / 4;

        int itemSpacing = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 12);
        if (width > height) {
            cardHeight = height - 4 * itemSpacing;
            cardWidth = 4 * cardHeight / 5;
            offset = (width - cardWidth) / 2;
        } else {
            offset = width / 10;
            cardWidth = offset * 8;
            cardHeight = 5 * cardWidth / 4;
        }
        if(cardWidth > maxWidth) {
            cardWidth = maxWidth;
            cardHeight = maxHeight;
            offset = (width - cardWidth) / 2;
        }

        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cardHeight));

        adapter.setDimens(cardWidth, cardHeight);

        slideLayoutManager.setPreviousItemPreview(offset);
        slideLayoutManager.setNextItemPreview(offset);
        slideLayoutManager.setItemSpacing(itemSpacing);
    }
    */

    /* class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
         private final WeakReference<ImageView> imageViewReference;
         private int data = 0;
         private int width;
         private int height;
         private Resources resources;

         public BitmapWorkerTask(ImageView imageView, int width, int height) {
             imageViewReference = new WeakReference<>(imageView);
             resources = getResources();
             this.width = width;
             this.height = height;
         }

         @Override
         protected Bitmap doInBackground(Integer... params) {
             data = params[0];
             Bitmap bitmap = decodeSampledBitmapFromResource(resources, data, width, height);
             addBitmapToMemoryCache(String.valueOf(data), bitmap);
             return bitmap;
         }

         @Override
         protected void onPostExecute(Bitmap bitmap) {
             if (bitmap != null) {
                 final ImageView imageView = imageViewReference.get();
                 if (imageView != null) {
                     imageView.setImageBitmap(bitmap);
                 }
             }
         }
     }

     */
    public class Destination implements Parcelable {

        public final Creator<Destination> CREATOR = new Creator<Destination>() {
            @Override
            public Destination[] newArray(int size) {
                return new Destination[size];
            }

            @Override
            public Destination createFromParcel(Parcel source) {
                return new Destination(source);
            }
        };
        public String title;
        public String info;
        public int src;
        public int color;
        public ArrayList<String> attractions = new ArrayList<String>();

        public Destination(Parcel source) {
            title = source.readString();
            info = source.readString();
            src = source.readInt();
            color = source.readInt();
            source.readStringList(attractions);
        }

        public Destination(String title, String info, int src, int color) {
            this.title = title;
            this.info = info;
            this.src = src;
            this.color = color;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(info);
            dest.writeInt(src);
            dest.writeInt(color);
            dest.writeStringList(attractions);
        }

        public void addAttractions(String... attractions) {
            this.attractions.addAll(Arrays.asList(attractions));
        }
    }

    public class DestinationViewHolder extends ListViewHolder {
        public ImageView destinationImage;
        public TextView destinationTitle;
        public TextView destinationInfo;
        //public ViewGroup destinationEnquiryLayout;
        //public Button destinationEnquiry;
        public View separator;
        public ViewGroup layout;
        public int position;

        private Context context;

        public DestinationViewHolder(final View itemView) {
            super(itemView);

            this.context = itemView.getContext();
            this.destinationImage = (ImageView) itemView.findViewById(R.id.destinationImage);
            this.destinationTitle = (TextView) itemView.findViewById(R.id.destinationTitle);
            this.destinationInfo = (TextView) itemView.findViewById(R.id.destinationInfo);
            //this.destinationEnquiry = (Button) itemView.findViewById(R.id.destinationEnquiry);
            /*this.destinationEnquiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEnquiry();
                }
            });
            */
            this.layout = (ViewGroup) itemView.findViewById(R.id.scrollingLayout);
            //this.destinationEnquiryLayout = (ViewGroup) itemView.findViewById(R.id.destinationEnquiryLayout);
            this.separator = itemView.findViewById(R.id.separator);
        }

        private void sendEnquiry() {
            Toast.makeText(this.context, "Enquiry sent.", Toast.LENGTH_SHORT).show();
        }
    }


}
