package com.example.knuverse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import com.kakao.sdk.navi.NaviClient
import com.kakao.sdk.navi.model.NaviOption
import com.kakao.sdk.navi.model.CoordType
import com.kakao.sdk.navi.model.Location

// 윗부분 중요한 코드 아님
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// 지도 펼치기를 눌렀을 때 반응하는 fragment
/**
 * A simple [Fragment] subclass.
 * Use the [kakaoMapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class kakaoMapFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kakao_map, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment kakaoMapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            kakaoMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    object Constants {
        const val WEB_NAVI_INSTALL = "https://app.link/to/install/kakao-navi"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name")
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")

        // 앱 설치 상태(카카오내비 호출)
        if (NaviClient.instance.isKakaoNaviInstalled(requireContext())) {
            startActivity(
                NaviClient.instance.navigateIntent(
                    //Location(name ?: "Unknown", longitude.toString(), latitude.toString()),
                    Location("카카오 판교오피스", "127.108640", "37.402111"),
                    NaviOption(coordType = CoordType.WGS84)
                )
            )
        } else { // 앱 미설치 상태(스토어 이동)
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.WEB_NAVI_INSTALL)
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}