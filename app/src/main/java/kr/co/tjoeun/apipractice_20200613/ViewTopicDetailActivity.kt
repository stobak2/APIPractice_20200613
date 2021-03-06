package kr.co.tjoeun.apipractice_20200613

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_topic_detail.*
import kr.co.tjoeun.apipractice_20200613.datas.Topic
import kr.co.tjoeun.apipractice_20200613.utils.ServerUtil
import org.json.JSONObject

class ViewTopicDetailActivity : BaseActivity() {

//    화면에서 넘겨준 주제 id값을 저장할 변수
    var mTopicId = -1
//    서버에서 받아온 주제 정보를 저장할 변수
    lateinit var mTopic : Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_topic_detail)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        voteToFirstBtn.setOnClickListener {

            ServerUtil.postRequestVote(mContext, mTopic.sides[0].id, object : ServerUtil.JsonResponseHandler {
                override fun onResponse(json: JSONObject) {

                    val code = json.getInt("code")

                    if (code == 200) {
                        runOnUiThread {
                            Toast.makeText(mContext, "참여해주셔서 감사합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        val message = json.getString("message")
                        runOnUiThread {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                        }
                    }

//                    투표가 끝나면 서버에서 다시 주제 진행 현황을 불러오자.
                    getTopicDataFromServer()
                }

            })
        }

        voteToSecondBtn.setOnClickListener {
            ServerUtil.postRequestVote(mContext, mTopic.sides[1].id, object : ServerUtil.JsonResponseHandler {
                override fun onResponse(json: JSONObject) {
                    val code = json.getInt("code")

                    if (code == 200) {
                        runOnUiThread {
                            Toast.makeText(mContext, "참여해주셔서 감사합니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        val message = json.getString("message")
                        runOnUiThread {
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                        }
                    }

//                    투표가 끝나면 서버에서 다시 주제 진행 현황을 불러오자.
                    getTopicDataFromServer()
                }

            })
        }

    }

    override fun setValues() {
        mTopicId = intent.getIntExtra("topic_id", -1)

        if (mTopicId == -1) {
//            주제 id가 -1로 남아있다 : topic_id 첨부가 제대로 되지 않았다.
            Toast.makeText(mContext, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
//            코드 추가 진행을 막자. (강제종료)
            return
        }

        Log.d("넘겨받은 주제 id", mTopicId.toString())

        getTopicDataFromServer()

    }

    fun getTopicDataFromServer() {

//        넘겨 받은 id값으로 서버에서 주제의 상세 진행 상황 받아오기
        ServerUtil.getRequestTopicDetail(mContext, mTopicId, object : ServerUtil.JsonResponseHandler {
            override fun onResponse(json: JSONObject) {

                val code = json.getInt("code")

                if (code == 200) {
                    val data = json.getJSONObject("data")
                    val topic = data.getJSONObject("topic")

//                    멤버변수 mTopic에 서버에서 내려준 내용을 파싱
                    mTopic = Topic.getTopicFromJson(topic)

                    runOnUiThread {

//                    받아온 주제의 제목을 화면에 표시
                        topicTitleTxt.text = mTopic.title

                        Glide.with(mContext).load(mTopic.imageUrl).into(topicImg)

//                        선택 진영 정보도 출력
                        firstSideTxt.text = mTopic.sides[0].title
                        secondSideTxt.text = mTopic.sides[1].title

//                        투표 현황도 파싱 된 데이터를 같이 사용.
                        firstSideVoteCountTxt.text = "${mTopic.sides[0].voteCount}표"
                        secondSideVoteCountTxt.text = "${mTopic.sides[1].voteCount}표"

                    }

                }

            }
        })
    }

}
