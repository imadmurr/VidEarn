package murr.imad.videarn.shared.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.Transformation
import androidx.recyclerview.widget.RecyclerView
import murr.imad.videarn.databinding.ItemFaqBinding

class FAQAdapter(
    private val questions: List<String>,
    private val answers: List<String>
) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(questions[position], answers[position])

        holder.itemView.setOnClickListener {
            if (holder.binding.answerTextView.visibility == View.GONE) {
                expand(holder.binding.answerTextView, holder.binding.cardView)
                rotate(holder.binding.imageViewArrow, 0f, 180f)
            } else {
                collapse(holder.binding.answerTextView)
                rotate(holder.binding.imageViewArrow, 180f, 0f)
            }
        }
    }

    override fun getItemCount() = questions.size

    private fun expand(view: View, cardView: View) {
        view.visibility = View.VISIBLE
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            (cardView.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration =
            (targetHeight / view.context.resources.displayMetrics.density).toInt().toLong()
        view.startAnimation(animation)
    }

    private fun collapse(view: View) {
        val initialHeight = view.measuredHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration =
            (initialHeight / view.context.resources.displayMetrics.density).toInt().toLong()
        view.startAnimation(animation)
    }

    private fun rotate(view: View, from: Float, to: Float) {
        val rotate = RotateAnimation(
            from, to,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        view.startAnimation(rotate)
    }

    class FAQViewHolder(val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: String, answer: String) {
            binding.questionTextView.text = question
            binding.answerTextView.text = answer
        }
    }
}
