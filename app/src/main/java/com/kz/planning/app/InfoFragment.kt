package com.kz.planning.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class InfoFragment : Fragment() {

    private val adapter by lazy { InfoAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv: RecyclerView = view.findViewById(R.id.info_rv)
        rv.adapter = adapter
        adapter.items = infoList
    }
}


class InfoAdapter : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {
    var items: List<InfoModel>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder =
        InfoViewHolder(parent.inflate(R.layout.info_item))

    fun ViewGroup.inflate(layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, false)

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        items?.get(position)?.let { holder.bind(it) }
    }

    inner class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val definition: TextView = view.findViewById(R.id.definition)

        fun bind(item: InfoModel) {
            title.text = item.term
            definition.text = item.definition
        }
    }
}


data class InfoModel(
    val term: String,
    val definition: String?,
)

val infoList = listOf(
    InfoModel(
        term = "Актив",
        definition = "Актив представляет собой финансовый ресурс или вещество, имеющее денежную ценность и которое владелец может использовать для генерации дохода или обмена на другие активы."
    ),
    InfoModel(
        term = "Дивиденды",
        definition = "Дивиденды - это доля прибыли компании, выплачиваемая ее акционерам в соответствии с количеством акций, которыми они владеют. Дивиденды являются одним из способов вознаграждения инвесторов и могут выплачиваться в виде денежных средств, акций или других активов."
    ),
    InfoModel(
        term = "Ипотека",
        definition = "Ипотека - это заем, который предоставляется банком или другим финансовым учреждением для покупки недвижимости. Заемщик обязуется выплачивать кредит с процентами в течение определенного периода, обычно на протяжении нескольких лет или десятилетий."
    ),
    InfoModel(
        term = "Капитал",
        definition = "Капитал представляет собой финансовый ресурс, который используется для инвестиций в предприятия, приобретения активов и финансирования бизнес-операций. Он может быть представлен денежными средствами, оборудованием, недвижимостью или другими активами."
    ),
    InfoModel(
        term = "Ликвидность",
        definition = "Ликвидность относится к способности актива быть быстро и без потерь превращенным в денежные средства. Актив с высокой ликвидностью может быть легко продан или конвертирован в наличные деньги, в то время как актив с низкой ликвидностью может быть труднее продать или преобразовать."
    ),
    InfoModel(
        term = "Облигация",
        definition = "Облигация - это финансовый инструмент, который представляет собой долговое обязательство эмитента перед владельцем. Облигации часто выпускаются компаниями и правительствами для привлечения капитала. Владелец облигации имеет право на получение процентных платежей и возврат основной суммы в определенные сроки."
    ),
    InfoModel(
        term = "Портфель",
        definition = "Портфель представляет собой комбинацию инвестиций и активов, управляемых инвестором или финансовым учреждением. Целью портфеля является достижение определенных финансовых целей, таких как увеличение капитала или диверсификация рисков."
    ),
    InfoModel(
        term = "Страхование",
        definition = "Страхование - это процесс защиты от финансовых потерь путем переноса риска на страховую компанию. Страхование может касаться различных областей, включая здоровье, автомобили, недвижимость и жизнь, и обычно включает выплату премий за страховую защиту."
    ),
    InfoModel(
        term = "Фондовый рынок",
        definition = "Фондовый рынок - это рынок, на котором проводятся торги акциями компаний и другими финансовыми инструментами. Это место, где инвесторы могут купить и продать ценные бумаги с целью получения дохода или роста капитала."
    ),
    InfoModel(
        term = "Хеджирование",
        definition = "Хеджирование - это стратегия управления рисками, которая используется для снижения потенциальных убытков от неблагоприятных движений цен или стоимости активов. Хеджирование часто осуществляется с помощью производных финансовых инструментов, таких как опционы и фьючерсы."
    )
)