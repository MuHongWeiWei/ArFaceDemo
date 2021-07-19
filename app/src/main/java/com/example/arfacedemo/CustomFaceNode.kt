package com.example.arfacedemo

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import androidx.lifecycle.ViewModelProvider
import com.google.ar.core.AugmentedFace
import com.google.ar.core.AugmentedFace.RegionType
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.AugmentedFaceNode
import java.io.File
import java.io.FileOutputStream

class CustomFaceNode(
    augmentedFace: AugmentedFace?,
    private val mainActivity: MainActivity,
    private val sceneView: ArSceneView,
) : AugmentedFaceNode(augmentedFace) {


    private var eyeNodeLeft: Node? = null
    private var eyeNodeRight: Node? = null
    private var lowerLipNode: Node? = null
    private var upperLipNode: Node? = null
    private var leftGggNode: Node? = null
    private var rightGggNode: Node? = null
    private var midGggNode: Node? = null
    var turnRight = 0
    var turnLeft = 0
    var turnTop = 0
    var turnBottom = 0


    companion object {
        enum class FaceRegion {
            LEFT_EYE,
            RIGHT_EYE,
            LOWER_LIP,
            UPPER_LIP,
            LEFT_GGG,
            RIGHT_GGG,
            MID_GGG
        }

        var start = true
    }

    override fun onActivate() {
        super.onActivate()
        eyeNodeLeft = Node()
        eyeNodeLeft?.setParent(this)

        eyeNodeRight = Node()
        eyeNodeRight?.setParent(this)

        lowerLipNode = Node()
        lowerLipNode?.setParent(this)

        upperLipNode = Node()
        upperLipNode?.setParent(this)

        leftGggNode = Node()
        leftGggNode?.setParent(this)

        rightGggNode = Node()
        rightGggNode?.setParent(this)

        midGggNode = Node()
        midGggNode?.setParent(this)
    }

    private fun getRegionPose(region: FaceRegion): Vector3? {
        val buffer = augmentedFace?.meshVertices
        if (buffer != null) {
            return when (region) {
                FaceRegion.LEFT_EYE ->
                    Vector3(buffer.get(374 * 3), buffer.get(374 * 3 + 1), buffer.get(374 * 3 + 2))
                FaceRegion.RIGHT_EYE ->
                    Vector3(buffer.get(440 * 3), buffer.get(440 * 3 + 1), buffer.get(440 * 3 + 2))
                FaceRegion.LOWER_LIP ->
                    Vector3(buffer.get(17 * 3), buffer.get(17 * 3 + 1), buffer.get(17 * 3 + 2))
                FaceRegion.UPPER_LIP ->
                    Vector3(buffer.get(0 * 3), buffer.get(0 * 3 + 1), buffer.get(0 * 3 + 2))
                FaceRegion.LEFT_GGG ->
                    Vector3(buffer.get(409 * 3), buffer.get(409 * 3 + 1), buffer.get(409 * 3 + 2))
                FaceRegion.RIGHT_GGG ->
                    Vector3(buffer.get(185 * 3), buffer.get(185 * 3 + 1), buffer.get(185 * 3 + 2))
                FaceRegion.MID_GGG ->
                    Vector3(buffer.get(14 * 3), buffer.get(14 * 3 + 1), buffer.get(14 * 3 + 2))
            }
        }
        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)
        augmentedFace?.let { face ->
            getRegionPose(FaceRegion.LEFT_EYE)?.let {
                eyeNodeLeft?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                eyeNodeLeft?.localScale = Vector3(0.055f, 0.055f, 0.055f)
                eyeNodeLeft?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), -10f)
            }

            getRegionPose(FaceRegion.RIGHT_EYE)?.let {
                eyeNodeRight?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                eyeNodeRight?.localScale = Vector3(0.055f, 0.055f, 0.055f)
                eyeNodeRight?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, 1.0f), 10f)
            }

            getRegionPose(FaceRegion.UPPER_LIP)?.let {
                upperLipNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                upperLipNode?.localScale = Vector3(0.04f, 0.04f, 0.04f)
            }

            getRegionPose(FaceRegion.LOWER_LIP)?.let {
                lowerLipNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                lowerLipNode?.localScale = Vector3(0.04f, 0.04f, 0.04f)
            }

            getRegionPose(FaceRegion.LEFT_GGG)?.let {
                leftGggNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                leftGggNode?.localScale = Vector3(0.04f, 0.04f, 0.04f)
            }

            getRegionPose(FaceRegion.RIGHT_GGG)?.let {
                rightGggNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                rightGggNode?.localScale = Vector3(0.04f, 0.04f, 0.04f)
            }

            getRegionPose(FaceRegion.MID_GGG)?.let {
                midGggNode?.localPosition = Vector3(it.x, it.y - 0.035f, it.z + 0.015f)
                midGggNode?.localScale = Vector3(0.04f, 0.04f, 0.04f)
            }

            val faceCheckVM = ViewModelProvider(mainActivity).get(FaceCheckVM::class.java)


            //低頭
            if (face.getRegionPose(RegionType.FOREHEAD_LEFT).yAxis[2] < -0.4 && turnTop == 0) {
                faceCheckVM.text.value = "請點頭"
                ++turnTop
            }

            //抬頭
            if (face.getRegionPose(RegionType.FOREHEAD_LEFT).yAxis[2] > 0.01 && turnBottom == 0) {
                faceCheckVM.text.value = "請抬頭"
                ++turnBottom
            }

            //點頭判定成功
            if (turnTop > 0 && turnBottom > 0) {
                turnTop = -1
                turnBottom = -1
                faceCheckVM.text.value = "點頭判定成功"
            }


            //微笑相關
            if ((leftGggNode!!.worldPosition.y - midGggNode!!.worldPosition.y) > 0.006 &&
                (rightGggNode!!.worldPosition.y - midGggNode!!.worldPosition.y) > 0.006 &&
                turnTop == -1 &&
                turnBottom == -1
            ) {
                faceCheckVM.text.value = "微笑判定成功"
                takePicture()
            }

            //搖頭相關
            if (face.centerPose.xAxis[2] > 0.4 && turnRight == 0) {
                ++turnRight
                faceCheckVM.text.value = "請轉向左"
            }

            if (face.centerPose.xAxis[2] < -0.4 && turnLeft == 0) {
                ++turnLeft
                faceCheckVM.text.value = "請轉向右"
            }

            if (turnRight > 0 && turnLeft > 0) {
                turnRight = -1
                turnLeft = -1
                faceCheckVM.text.value = "搖頭判定成功"
            }
        }
    }

    private fun takePicture() {
        if (start) {
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()

            val bitmap = Bitmap.createBitmap(
                sceneView.width,
                sceneView.height,
                Bitmap.Config.ARGB_8888
            )

            PixelCopy.request(sceneView, bitmap, {
                val name = "${System.currentTimeMillis()}face.jpg"
                val file =
                    File(mainActivity.getExternalFilesDir("face")?.absolutePath, name)
                val ops = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops)
            }, Handler(handlerThread.looper))
            start = false
        }
    }
}