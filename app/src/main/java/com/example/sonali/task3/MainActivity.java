package com.example.sonali.task3;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btn_address, btn_submit, btn_display;
    EditText et_location,et_name;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ImageView imageView;
    Spinner spinner;

    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    int lat, lon;

    String[] role = {"A", "B", "C"};
    String selected_role = null;

    Bitmap bmp = null;
    private static final int GALLERY_REQUEST = 1;
    //private static final int CAMERA_REQUEST = 2;
    //private static final int CAMERA_PERMISSION_CODE = 3;
    private static final int LOCATION_PERMISSION_CODE = 2;

    NotificationCompat.Builder notification;
    private static final int ID = 1;
    public static final String CHANNEL_1_ID = "channel1";

    MyDBHandler dbHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        et_location = (EditText) findViewById(R.id.editText2);
        et_name = (EditText) findViewById(R.id.editText1);
        btn_address = (Button) findViewById(R.id.btn_address);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_display = (Button) findViewById(R.id.btn_display);
        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        spinner = (Spinner) findViewById(R.id.spinner);
        imageView = (ImageView) findViewById(R.id.imageView);
        notification = new NotificationCompat.Builder(this, CHANNEL_1_ID);

        spinner.setOnItemSelectedListener(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, role);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            return;
        } else {
            buildLocationRequest();
            buildLocationCallback();
            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
            btn_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                }
            });
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        dbHandler = new MyDBHandler(this, null, null, 1);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                    return;
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                String name=et_name.getText().toString();
                String role=selected_role;
                String location=et_location.getText().toString();

                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte imageInByte[] = stream.toByteArray();

                dbHandler.addPeople(name,location,role,imageInByte);
                Toast.makeText(MainActivity.this, "Data Entered", Toast.LENGTH_SHORT).show();

                et_name.setText("");
                et_location.setText("");
                Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.camera);
                imageView.setImageBitmap(img);

                //ListView
                /*ArrayList<String> list = new ArrayList<String>();
                Cursor data=dbHandler.getAllRows();
                while(data.moveToNext())
                {
                    list.add(data.getString(3));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,list);
                    display_info.setAdapter(adapter);

                }*/

                /*ArrayList<String> list = new ArrayList<String>();
                list=dbHandler.getData();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,list));
                display_info.setAdapter(adapter);
                 list.add(et_name.getText().toString());
                et_nChame.setText("");
                adapter.notifyDataSetanged();*/

                createNotification();
            }
        });

        btn_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<persons> list=new ArrayList<persons>();
                Cursor c=dbHandler.getAllPersons();
                while(c.moveToNext())
                {
                    String name=c.getString(0);
                    String location=c.getString(1);
                    String role=c.getString(2);
                    byte[] img=c.getBlob(3);
                    persons p=new persons(name,location,role,img);
                    list.add(p);
                }

                if(!(list.size()<1))
                {
                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    RecyclerView.Adapter adapter;
                    adapter = new MyAdapter(MainActivity.this,list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(this, role[position], Toast.LENGTH_SHORT).show();
        selected_role=role[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void buildLocationRequest(){
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
    }

    public void buildLocationCallback(){
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location:locationResult.getLocations()) {
                    lat=(int)location.getLatitude();
                    lon=(int)location.getLongitude();
                    fetchLocation();
                }
            }
        };
    }

    public void fetchLocation(){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                //for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                // sb.append(address.getAddressLine(i)).append("\n");
                //sb.append(address.getLocality()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getFeatureName());
                et_location.setText(sb.toString());
            }

            else{
                et_location.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            et_location.setText("Cannot get Address!");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
                }else{
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            /*case CAMERA_PERMISSION_CODE: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "test.jpg");
                    Uri tempUri = Uri.fromFile(imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    try {
                        bmp = getBitmapFromUri(selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bmp);
                } else {
                    Toast.makeText(this, "you havent chosen an Image", Toast.LENGTH_SHORT).show();
                }
                break;

            /*case CAMERA_REQUEST:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap bmp = null;
                        try {
                            bmp = getBitmapFromUri(selectedImage);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(bmp);
                    } else {
                        Toast.makeText(this, "you havent chosen an Image", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }*/
        }
        }

   public void getImage()
   {
       final CharSequence[] options={"Select from Gallery","Cancel"};
       AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
       builder.setTitle("Add Photo!");
       builder.setItems(options, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int item) {
               if(options[item].equals("Select from Gallery")){

                   Intent gallery_intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   gallery_intent.setType("image/*");
                   startActivityForResult(Intent.createChooser(gallery_intent,"Select Picture"),GALLERY_REQUEST);
               }

               /*else if(options[item].equals("Take Photo")){

                       if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)
                               != PackageManager.PERMISSION_GRANTED) {
                           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                       } else {
                           Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           imageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"test.jpg");
                           Uri tempUri=Uri.fromFile(imageFile);
                           cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
                           cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                           startActivityForResult(cameraIntent, CAMERA_REQUEST);
                       }
                   }*/
               else{
                   Toast.makeText(MainActivity.this, "Please select a photo", Toast.LENGTH_SHORT).show();
               }

               }

       });
       builder.show();
   }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void createNotification()
    {
        String name=et_name.getText().toString();
        String location=et_location.getText().toString();
        Bitmap bMap = bmp;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Channel1",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("this is channel1");

            NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(manager!=null) {
                manager.createNotificationChannel(channel);
            }

        }
        notification.setSmallIcon(R.drawable.ic_announcement);
        notification.setLargeIcon(bMap);

        notification.setWhen(System.currentTimeMillis());
        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification.setContentTitle("You have a new Notification");
        notification.setContentText(name+"\n Location: "+location+"\n Role: "+selected_role);

        Intent intent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(ID,notification.build());
    }

        /*public void getLocation() {
        /*locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = (int) (location.getLatitude());
                lon = (int) (location.getLongitude());
                //latitudeField.setText(Integer.toString(lat)+"\n"+Integer.toString(lon));
                new ReverseGeocodingTask(getApplicationContext()).execute(new Location[] {location});

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
            return;
        }else{
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationlistener);
           buildLocationRequest();
           buildLocationCallback();
        }
    }*/

    /*private class ReverseGeocodingTask extends AsyncTask<Location, Void,StringBuilder> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected StringBuilder doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            StringBuilder sb = new StringBuilder();
            Location loc = params[0];
            try {
                List<Address> addresses = geocoder.getFromLocation(222.5726, 88.3639, 1);

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    sb.append(address.getSubAdminArea());

                }
                else{
                    sb.append("No Address returned!");
                }
            }catch (IOException e) {
                e.printStackTrace();
                sb.append("Cannot get Address!");
            }
            return sb;
        }

       @Override
       protected void onPostExecute(StringBuilder sb) {
            s=sb.toString();
           et_location.setText(s);
       }
   }*/
}
