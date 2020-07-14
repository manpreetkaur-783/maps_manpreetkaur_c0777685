package com.manpreet.maps_manpreetkaur_c0777685;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker a, b, c, d;
    Polyline ab, bc, cd, da;
    Polygon shape;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(56.85, -124.83);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 2));


        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

                float[] results = new float[1];
                Location.distanceBetween(polyline.getPoints().get(0).latitude, polyline.getPoints().get(0).longitude,
                        polyline.getPoints().get(1).latitude, polyline.getPoints().get(1).longitude, results);
                float distance = results[0];
                LatLng point = new LatLng((polyline.getPoints().get(0).latitude + polyline.getPoints().get(1).latitude) / 2, (polyline.getPoints().get(0).longitude + polyline.getPoints().get(1).longitude) / 2);

                MarkerOptions markerOptions = new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromBitmap(textAsBitmap(distance / 1000 + " Km", 55, getColor(R.color.orange))))
                        .position(point);


                mMap.addMarker(markerOptions);
//                addText(MapsActivity.this, googleMap, polyline.getPoints().get(0), distance + "", 10, 20);
//                Toast.makeText(MapsActivity.this, "click " + distance, Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                clearMap();
                if (a != null && b != null) {
                    ab = drawLine(a, b);
                    ab.setClickable(true);
                }
                if (c != null && b != null) {
                    bc.setClickable(true);
                    bc = drawLine(b, c);
                    bc.setClickable(true);
                }
                if (c != null && d != null) {
                    cd = drawLine(c, d);
                    cd.setClickable(true);
                }
                if (d != null && a != null) {
                    da = drawLine(d, a);
                    da.setClickable(true);
                }
//                if (markers.size() == POLYGON_SIDES)
                drawShape();

            }
        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(MapsActivity.this, " got clicked ", Toast.LENGTH_SHORT).show(); //do some stuff
//                return false;
//            }
//        });
        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                Location location = new Location("Your Destination");
//                location.setLatitude(latLng.latitude);
//                location.setLongitude(latLng.longitude);
                // set marker
                setMarker(latLng);
//                if (d!=null) {
//                    isMarkerClick(latLng, a, "a");
//                    isMarkerClick(latLng, b, "b");
//                    isMarkerClick(latLng, c, "c");
//                    isMarkerClick(latLng, d, "d");
//                }
            }
        });
    }

    void isMarkerClick(LatLng latLng, Marker marker, String text) {
        if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
            Toast.makeText(MapsActivity.this, text + " got clicked " + marker.getPosition(), Toast.LENGTH_SHORT).show(); //do some stuff
        }
    }

    private void setMarker(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String province = addresses.get(0).getAdminArea();
            String subThoroughfare = addresses.get(0).getSubThoroughfare();
            String postalCode = addresses.get(0).getPostalCode();
            String thoroughfare = addresses.get(0).getThoroughfare();

            MarkerOptions options;
            options = new MarkerOptions().position(latLng)
                    .title(thoroughfare + ", " + subThoroughfare + ", " + postalCode)
                    .draggable(true)
                    .snippet(city + ", " + province);

            if (a == null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap("A", 55.0f, getColor(android.R.color.holo_blue_bright))));
                a = mMap.addMarker(options);
            } else if (b == null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap("B", 55.0f, getColor(android.R.color.holo_blue_bright))));
                b = mMap.addMarker(options);
                ab = drawLine(a, b);
                ab.setClickable(true);
            } else if (c == null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap("C", 55.0f, getColor(android.R.color.holo_blue_bright))));
                c = mMap.addMarker(options);
                bc = drawLine(b, c);
                bc.setClickable(true);
            } else if (d == null) {
                options.icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap("D", 55.0f, getColor(android.R.color.holo_blue_bright))));
                d = mMap.addMarker(options);
                cd = drawLine(c, d);
                cd.setClickable(true);
                da = drawLine(d, a);
                da.setClickable(true);
                drawShape();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(getColor(R.color.greem));

        options.add(a.getPosition());
        options.add(b.getPosition());
        options.add(c.getPosition());
        options.add(d.getPosition());
        shape = mMap.addPolygon(options);

    }

    Polyline drawLine(Marker homeMarker, Marker destMarker) {
        PolylineOptions options = new PolylineOptions()
                .color(Color.RED)
                .width(10)
                .add(homeMarker.getPosition(), destMarker.getPosition());

        return mMap.addPolyline(options);
    }

    private void clearMap() {

        ab.remove();
        bc.remove();
        cd.remove();
        da.remove();

        if (shape != null) {
            shape.remove();
            shape = null;
        }
    }
    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
    public static Bitmap drawText(String text, int textWidth, int textSize) {
// Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

// Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

// Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        c.drawPaint(paint);

// Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


}

