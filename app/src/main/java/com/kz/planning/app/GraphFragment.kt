package com.kz.planning.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.kz.planning.app.databinding.DialogLayoutBinding
import com.kz.planning.app.databinding.FragmentGraphBinding
import io.paperdb.Paper

class GraphFragment : Fragment() {
    private val targetProgress = MutableLiveData<Double>(0.0)
    private val target = MutableLiveData<Double>(10000.0)
    private val targetText = MutableLiveData<String>("0/10000")
    private val list = MutableLiveData<MutableList<Double>>(mutableListOf())

    private var mBinding: FragmentGraphBinding? = null
    private val binding get() = mBinding!!
    val adapter by lazy {
        CoinListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setTargetText() {
        val string = "${targetProgress.value!!.toInt()}/${target.value!!.toInt()}"
        targetText.postValue(string)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            Paper.book().read<MutableList<Double>>(KEY_1)?.let {
                list.postValue(it)
                targetProgress.postValue(it.sum())
            }
            Paper.book().read<Double>(KEY_2)?.let {
                target.postValue(it)
            }
        } catch (_: Throwable) {

        }
        binding.updateBtn.setOnClickListener {
            setDialog()
        }
        binding.plusBtn.setOnClickListener {
            try {
                val newList = list.value!!.apply {
                    add(binding.amountInput.text.toString().toDouble() * -1)
                }
                binding.progressCircle.progress = newList.sum().toInt()
                targetProgress.postValue(newList.sum())
                list.postValue(newList)
            } catch (e: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Пожалуйста введите корректное значение",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        binding.minusBtn.setOnClickListener {
            try {
                val newList = list.value!!.apply {
                    add(binding.amountInput.text.toString().toDouble())
                }
                binding.progressCircle.progress = newList.sum().toInt()
                targetProgress.postValue(newList.sum())
                list.postValue(newList)
            } catch (e: Throwable) {
                Toast.makeText(requireContext(), "Please enter correct value", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.rcv.adapter = adapter
        initViewModel()
    }

    private fun initViewModel() {
        target.observe(viewLifecycleOwner) {
            try {
                Paper.book().write(KEY_2, it)
                binding.progressCircle.max = it.toInt()
                setTargetText()
            } catch (_: Throwable) {

            }
        }
        targetProgress.observe(viewLifecycleOwner) {
            try {
                binding.progressCircle.progress = it.toInt()
                setTargetText()
            } catch (_: Throwable) {

            }
        }
        list.observe(viewLifecycleOwner) {
            adapter.list = it
            Paper.book().write(KEY_1, it)
            targetProgress.postValue(it.sum())
            setTargetText()
        }
        targetText.observe(viewLifecycleOwner) {
            binding.progressTv.text = it
        }
    }

    private fun setDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btn.setOnClickListener {
            try {
                if (dialogBinding.amountInput.text.isNullOrEmpty()) {
                    dialog.dismiss()
                } else {
                    val newTarget = dialogBinding.amountInput.text.toString().toDouble()
                    list.postValue(mutableListOf())
                    target.postValue(newTarget)
                    dialog.dismiss()
                }
            } catch (e: Throwable) {
                Toast.makeText(requireContext(), "Ошибка ввода", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()

    }

    companion object {
        const val KEY_1 = "KEY_1"
        const val KEY_2 = "KEY_2"
    }
}


class CoinListAdapter() : RecyclerView.Adapter<CoinListAdapter.CoinInfoViewHolder>() {

    var list: List<Double> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        return CoinInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.price_layout, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class CoinInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.tv)

        @SuppressLint("SetTextI18n")
        fun bind(item: Double) {
            textView.setTextColor(textView.context.getColor(R.color.red))
            if (list[position] > 0) {
                textView.text = "+" + list[position].toString()
            } else {
                textView.text = list[position].toString()
            }
        }
    }
}