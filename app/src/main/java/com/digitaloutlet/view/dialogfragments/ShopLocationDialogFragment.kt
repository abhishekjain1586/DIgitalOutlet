package com.digitaloutlet.view.dialogfragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.digitaloutlet.R
import com.digitaloutlet.adapter.AddressAdapter
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.utils.DOUtils
import com.digitaloutlet.utils.GeocoderUtil
import com.digitaloutlet.utils.view.BottomSheetMapView
import com.digitaloutlet.viewholder.AddressViewHolder
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import kotlin.collections.ArrayList


class ShopLocationDialogFragment : BottomSheetDialogFragment(), View.OnClickListener, OnMapReadyCallback ,
    AddressViewHolder.OnAddressItemClickListener {

    val TAG = BusinessTypeDialogFragment::class.java.simpleName

    private lateinit var btmShtLayout: LinearLayout
    private lateinit var ivCLose: ImageView
    private lateinit var mapView: BottomSheetMapView
    private lateinit var mMTvAddress: MultiAutoCompleteTextView
    private lateinit var edtPincode: EditText

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var contentView: View
    private var mIsExpandHeightSet = false
    private var mGoogleMap: GoogleMap? = null
    private var mAdapter: AddressAdapter? = null
    private var mAddressLst = ArrayList<String>()

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.dialog_fragment_shop_location, container, false)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(savedInstanceState)
    }

    private fun initView(savedInstanceState: Bundle?) {
        ivCLose = contentView.findViewById(R.id.iv_close)
        btmShtLayout = contentView.findViewById(R.id.bottomSheet)
        mapView = contentView.findViewById(R.id.map)
        mMTvAddress = contentView.findViewById(R.id.mac_tv_address)
        edtPincode = contentView.findViewById(R.id.edt_pincode)

        bottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO //(DOApplication._INSTANCE.getWindowHeight()*0.4).toInt()

        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.activity)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }

        setCallback()
        initAddressAdapter()
    }

    private fun initAddressAdapter() {
        if (mAdapter == null) {
            mAdapter = AddressAdapter(requireContext())
            mAdapter?.setData(mAddressLst)
            mAdapter?.setItemClickListener(this)
            mMTvAddress.setAdapter(mAdapter)
            mMTvAddress.threshold = 3
            mMTvAddress.bringToFront()
            contentView.findViewById<RelativeLayout>(R.id.lay_address).bringToFront()

            mMTvAddress.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d("selected", ""+adapterView?.getItemAtPosition(position) as String)
                }

            })

            mMTvAddress.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    val strAddress = mMTvAddress.text
                    if (strAddress.isNotEmpty() && strAddress.length >= 3) {
                        getAddress(strAddress.toString())
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

            })
        }
    }

    private fun getAddress(strAddress: String) {
        //GeocoderUtil.getInstance(requireContext()).getAddressFromPlacesAPI(strAddress)

        GeocoderUtil.getInstance(requireContext()).getAddress(strAddress, object : GeocoderUtil.OnGeocoderAddressListener {
            override fun onAddressReceived(addressLst: List<Address>) {
                Log.d("Pincode", ""+addressLst.get(0).latitude + " // " + addressLst.get(0).longitude)
                val newLocation = LatLng(addressLst.get(0).latitude, addressLst.get(0).longitude)

                mGoogleMap?.clear()
                mGoogleMap?.addMarker(MarkerOptions().position(newLocation))
                mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(newLocation))
                mGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
            }

            override fun onError() {
                Log.d("Pincode", "Address Not Found")
            }

        })
    }

    private fun setCallback() {
        ivCLose.setOnClickListener(this)
        mapView.getMapAsync(this)

        edtPincode.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(tv: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (edtPincode.text.isNotEmpty()) {
                        getAddress(edtPincode.text.toString())
                        return true
                    }
                }
                return false
            }

        })

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    Log.d(TAG, "STATE_EXPANDED")
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    Log.d(TAG, "STATE_COLLAPSED")
                }
                if (BottomSheetBehavior.STATE_DRAGGING == i) {
                    Log.d(TAG, "STATE_DRAGGING")

                    if (!mIsExpandHeightSet && (btmShtLayout.height > DOApplication._INSTANCE.getWindowHeight() - DOUtils.getActionBarSize(requireContext()) - DOUtils.getStatusBarHeight(requireActivity()))) {
                        val params = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        params.height = DOApplication._INSTANCE.getWindowHeight() - DOUtils.getActionBarSize(requireContext()) - DOUtils.getStatusBarHeight(requireActivity())
                        btmShtLayout.layoutParams = params
                    }
                    mIsExpandHeightSet = true
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    Log.d(TAG, "STATE_HIDDEN")
                    dismiss()
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })
    }

    override fun onItemClicked(address: String) {

    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.iv_close -> {
                dismiss()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        // Add a marker in Sydney, Australia, and move the camera.
        val sydney = LatLng(-34.0, 151.0)
        mGoogleMap?.setMinZoomPreference(1.0f);
        //googleMap?.setMaxZoomPreference(14.0f);
        mGoogleMap?.addMarker(MarkerOptions().position(sydney))//.title("Marker in Sydney"))
        mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mGoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(11.0f))

        mGoogleMap?.setOnMapLongClickListener(object : GoogleMap.OnMapLongClickListener {
            override fun onMapLongClick(location: LatLng?) {
                location?.let {
                    mGoogleMap?.clear()
                    mGoogleMap?.addMarker(MarkerOptions().position(it))
                    mGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(it))
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}