package com.kz.planning.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kz.planning.app.databinding.CurrencyItemBinding
import com.kz.planning.app.databinding.FragmentCurencyBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.math.BigInteger


class CurrencyFragment : Fragment() {


    private val currencyList = MutableLiveData<List<CurrencyRecyclerViewAdapter.ItemModel>>()
    private val currencyListLiveData: LiveData<List<CurrencyRecyclerViewAdapter.ItemModel>>
        get() = currencyList

    private val error = MutableLiveData<String>()
    private val errorLiveData: LiveData<String>
        get() = error

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var binding: FragmentCurencyBinding? = null
    private val adapter = CurrencyRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurencyBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeError()
        getListCurrency()
    }

    private fun getListCurrency() {
        lifecycleScope.launch {
            try {
                val response = retrofit.create(ValuteApi::class.java).getCurrencyList("usd")
                currencyList.postValue(response)
            } catch (e: IOException) {
                val data = "[]" // Предположим, что у вас есть заглушка данных
                currencyList.postValue(
                    Gson().fromJson(
                        data,
                        object : TypeToken<List<CurrencyRecyclerViewAdapter.ItemModel>>() {}.type
                    )
                )
                error.postValue("Ошибка подключения")
            }
        }
    }

    private fun setupRecyclerView() {
        binding?.currencyRv?.adapter = adapter
        currencyListLiveData.observe(viewLifecycleOwner) { currencyList ->
            adapter.setItem(currencyList)
        }
    }

    private fun observeError() {
        errorLiveData.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}


class CurrencyRecyclerViewAdapter(private var listItems: List<ItemModel>? = null) :
    RecyclerView.Adapter<CurrencyRecyclerViewAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding =
            CurrencyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun getItemCount(): Int = listItems?.size ?: 0

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        listItems?.let {
            holder.bind(it[position])
        }
    }

    fun setItem(items: List<ItemModel>) {
        listItems = items
        notifyDataSetChanged()
    }

    inner class CurrencyViewHolder(private val binding: CurrencyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemModel) {
            binding.currencyCodeTv.text = item.id
            binding.currencyRateTv.text = String.format("%.3f USD", item.current_price)
            val priceChangePercentage = if (item.price_change_percentage_24h > 0) {
                String.format("+%.2f %%", item.price_change_percentage_24h)
            } else String.format("%.2f %%", item.price_change_percentage_24h)
            binding.currecyUpDownTv.text = priceChangePercentage
            binding.cardUpDown.setCardBackgroundColor(
                binding.currecyUpDownTv.context.getColor(
                    if (item.price_change_percentage_24h > 0) R.color.green else R.color.red
                )
            )
        }
    }

    data class ItemModel(
        val ath: Double,
        val ath_change_percentage: Double,
        val ath_date: String,
        val atl: Double,
        val atl_change_percentage: Double,
        val atl_date: String,
        val circulating_supply: Double,
        val current_price: Double,
        val fully_diluted_valuation: BigInteger,
        val high_24h: Double,
        val id: String,
        val image: String,
        val last_updated: String,
        val low_24h: Double,
        val market_cap: BigInteger,
        val market_cap_change_24h: Double,
        val market_cap_change_percentage_24h: Double,
        val market_cap_rank: BigInteger,
        val max_supply: Double,
        val name: String,
        val price_change_24h: Double,
        val price_change_percentage_24h: Double,
        val symbol: String,
        val total_supply: Double,
        val total_volume: Double,
    )
}

val data =
    "[{\"id\":\"bitcoin\",\"symbol\":\"btc\",\"name\":\"Bitcoin\",\"image\":\"https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579\",\"current_price\":27952,\"market_cap\":540877853736,\"market_cap_rank\":1,\"fully_diluted_valuation\":587248080760,\"total_volume\":9484905396,\"high_24h\":28142,\"low_24h\":27879,\"price_change_24h\":-60.83268005567152,\"price_change_percentage_24h\":-0.21716,\"market_cap_change_24h\":-137525786.82281494,\"market_cap_change_percentage_24h\":-0.02542,\"circulating_supply\":19341800.0,\"total_supply\":21000000.0,\"max_supply\":21000000.0,\"ath\":69045,\"ath_change_percentage\":-59.49846,\"ath_date\":\"2021-11-10T14:24:11.849Z\",\"atl\":67.81,\"atl_change_percentage\":41139.65008,\"atl_date\":\"2013-07-06T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:52.878Z\"},{\"id\":\"ethereum\",\"symbol\":\"eth\",\"name\":\"Ethereum\",\"image\":\"https://assets.coingecko.com/coins/images/279/large/ethereum.png?1595348880\",\"current_price\":1843.17,\"market_cap\":222075287280,\"market_cap_rank\":2,\"fully_diluted_valuation\":222075287280,\"total_volume\":6748649402,\"high_24h\":1864.58,\"low_24h\":1833.76,\"price_change_24h\":-21.417763074402956,\"price_change_percentage_24h\":-1.14866,\"market_cap_change_24h\":-2072390851.4385376,\"market_cap_change_percentage_24h\":-0.92456,\"circulating_supply\":120439021.775447,\"total_supply\":120439021.775447,\"max_supply\":null,\"ath\":4878.26,\"ath_change_percentage\":-62.2019,\"ath_date\":\"2021-11-10T14:24:19.604Z\",\"atl\":0.432979,\"atl_change_percentage\":425761.3852,\"atl_date\":\"2015-10-20T00:00:00.000Z\",\"roi\":{\"times\":87.16371260893206,\"currency\":\"btc\",\"percentage\":8716.371260893206},\"last_updated\":\"2023-04-09T18:22:55.492Z\"},{\"id\":\"tether\",\"symbol\":\"usdt\",\"name\":\"Tether\",\"image\":\"https://assets.coingecko.com/coins/images/325/large/Tether.png?1668148663\",\"current_price\":1.001,\"market_cap\":80328550411,\"market_cap_rank\":3,\"fully_diluted_valuation\":80328550411,\"total_volume\":17838101110,\"high_24h\":1.004,\"low_24h\":1.0,\"price_change_24h\":0.0007149,\"price_change_percentage_24h\":0.07144,\"market_cap_change_24h\":72506503,\"market_cap_change_percentage_24h\":0.09034,\"circulating_supply\":80210538011.0731,\"total_supply\":80210538011.0731,\"max_supply\":null,\"ath\":1.32,\"ath_change_percentage\":-24.30845,\"ath_date\":\"2018-07-24T00:00:00.000Z\",\"atl\":0.572521,\"atl_change_percentage\":74.92307,\"atl_date\":\"2015-03-02T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:20:00.633Z\"},{\"id\":\"binancecoin\",\"symbol\":\"bnb\",\"name\":\"BNB\",\"image\":\"https://assets.coingecko.com/coins/images/825/large/bnb-icon2_2x.png?1644979850\",\"current_price\":311.1,\"market_cap\":49136497477,\"market_cap_rank\":4,\"fully_diluted_valuation\":62239367500,\"total_volume\":399191388,\"high_24h\":312.4,\"low_24h\":309.98,\"price_change_24h\":-1.2929094880549314,\"price_change_percentage_24h\":-0.41387,\"market_cap_change_24h\":-137819950.34310913,\"market_cap_change_percentage_24h\":-0.2797,\"circulating_supply\":157895234.0,\"total_supply\":157900174.0,\"max_supply\":200000000.0,\"ath\":686.31,\"ath_change_percentage\":-54.6563,\"ath_date\":\"2021-05-10T07:24:17.097Z\",\"atl\":0.0398177,\"atl_change_percentage\":781454.08142,\"atl_date\":\"2017-10-19T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:23:01.402Z\"},{\"id\":\"usd-coin\",\"symbol\":\"usdc\",\"name\":\"USD Coin\",\"image\":\"https://assets.coingecko.com/coins/images/6319/large/USD_Coin_icon.png?1547042389\",\"current_price\":0.999963,\"market_cap\":32613378582,\"market_cap_rank\":5,\"fully_diluted_valuation\":32613378582,\"total_volume\":2146148283,\"high_24h\":1.003,\"low_24h\":0.997771,\"price_change_24h\":-0.001580142686383179,\"price_change_percentage_24h\":-0.15777,\"market_cap_change_24h\":7224817,\"market_cap_change_percentage_24h\":0.02216,\"circulating_supply\":32600464084.042,\"total_supply\":32600464084.042,\"max_supply\":null,\"ath\":1.17,\"ath_change_percentage\":-14.74605,\"ath_date\":\"2019-05-08T00:40:28.300Z\",\"atl\":0.877647,\"atl_change_percentage\":13.91581,\"atl_date\":\"2023-03-11T08:02:13.981Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:58.951Z\"},{\"id\":\"ripple\",\"symbol\":\"xrp\",\"name\":\"XRP\",\"image\":\"https://assets.coingecko.com/coins/images/44/large/xrp-symbol-white-128.png?1605778731\",\"current_price\":0.502866,\"market_cap\":26000075029,\"market_cap_rank\":6,\"fully_diluted_valuation\":50301497855,\"total_volume\":702679386,\"high_24h\":0.509875,\"low_24h\":0.501343,\"price_change_24h\":-0.005316517932608011,\"price_change_percentage_24h\":-1.04618,\"market_cap_change_24h\":-236446178.30241013,\"market_cap_change_percentage_24h\":-0.90121,\"circulating_supply\":51688470797.0,\"total_supply\":99989014677.0,\"max_supply\":100000000000.0,\"ath\":3.4,\"ath_change_percentage\":-85.19807,\"ath_date\":\"2018-01-07T00:00:00.000Z\",\"atl\":0.00268621,\"atl_change_percentage\":18626.63695,\"atl_date\":\"2014-05-22T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:58.831Z\"},{\"id\":\"cardano\",\"symbol\":\"ada\",\"name\":\"Cardano\",\"image\":\"https://assets.coingecko.com/coins/images/975/large/cardano.png?1547034860\",\"current_price\":0.388782,\"market_cap\":13628260017,\"market_cap_rank\":7,\"fully_diluted_valuation\":17499538772,\"total_volume\":206644288,\"high_24h\":0.390726,\"low_24h\":0.384119,\"price_change_24h\":0.00034827,\"price_change_percentage_24h\":0.08966,\"market_cap_change_24h\":38848641,\"market_cap_change_percentage_24h\":0.28587,\"circulating_supply\":35045020830.3234,\"total_supply\":45000000000.0,\"max_supply\":45000000000.0,\"ath\":3.09,\"ath_change_percentage\":-87.40234,\"ath_date\":\"2021-09-02T06:00:10.474Z\",\"atl\":0.01925275,\"atl_change_percentage\":1919.86054,\"atl_date\":\"2020-03-13T02:22:55.044Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:56.577Z\"},{\"id\":\"dogecoin\",\"symbol\":\"doge\",\"name\":\"Dogecoin\",\"image\":\"https://assets.coingecko.com/coins/images/5/large/dogecoin.png?1547792256\",\"current_price\":0.083238,\"market_cap\":11557314279,\"market_cap_rank\":8,\"fully_diluted_valuation\":null,\"total_volume\":788346449,\"high_24h\":0.083547,\"low_24h\":0.081036,\"price_change_24h\":0.00045146,\"price_change_percentage_24h\":0.54533,\"market_cap_change_24h\":78029017,\"market_cap_change_percentage_24h\":0.67974,\"circulating_supply\":138870246383.705,\"total_supply\":null,\"max_supply\":null,\"ath\":0.731578,\"ath_change_percentage\":-88.62922,\"ath_date\":\"2021-05-08T05:08:23.458Z\",\"atl\":8.69e-05,\"atl_change_percentage\":95622.18128,\"atl_date\":\"2015-05-06T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:51.870Z\"},{\"id\":\"staked-ether\",\"symbol\":\"steth\",\"name\":\"Lido Staked Ether\",\"image\":\"https://assets.coingecko.com/coins/images/13442/large/steth_logo.png?1608607546\",\"current_price\":1835.34,\"market_cap\":10889187907,\"market_cap_rank\":9,\"fully_diluted_valuation\":10889187907,\"total_volume\":38070463,\"high_24h\":1857.97,\"low_24h\":1823.05,\"price_change_24h\":-22.508747219473207,\"price_change_percentage_24h\":-1.21155,\"market_cap_change_24h\":-95418336.93718147,\"market_cap_change_percentage_24h\":-0.86866,\"circulating_supply\":5931178.54147606,\"total_supply\":5931178.54147606,\"max_supply\":5931178.54147606,\"ath\":4829.57,\"ath_change_percentage\":-61.99802,\"ath_date\":\"2021-11-10T14:40:47.256Z\",\"atl\":482.9,\"atl_change_percentage\":280.06765,\"atl_date\":\"2020-12-22T04:08:21.854Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:54.810Z\"},{\"id\":\"matic-network\",\"symbol\":\"matic\",\"name\":\"Polygon\",\"image\":\"https://assets.coingecko.com/coins/images/4713/large/matic-token-icon.png?1624446912\",\"current_price\":1.09,\"market_cap\":9995738428,\"market_cap_rank\":10,\"fully_diluted_valuation\":10907056757,\"total_volume\":214641633,\"high_24h\":1.11,\"low_24h\":1.087,\"price_change_24h\":-0.021656619416904777,\"price_change_percentage_24h\":-1.94832,\"market_cap_change_24h\":-176552104.0602131,\"market_cap_change_percentage_24h\":-1.73562,\"circulating_supply\":9164469069.28493,\"total_supply\":10000000000.0,\"max_supply\":10000000000.0,\"ath\":2.92,\"ath_change_percentage\":-62.61332,\"ath_date\":\"2021-12-27T02:08:34.307Z\",\"atl\":0.00314376,\"atl_change_percentage\":34581.20074,\"atl_date\":\"2019-05-10T00:00:00.000Z\",\"roi\":{\"times\":413.40886276365933,\"currency\":\"usd\",\"percentage\":41340.886276365934},\"last_updated\":\"2023-04-09T18:23:00.144Z\"},{\"id\":\"solana\",\"symbol\":\"sol\",\"name\":\"Solana\",\"image\":\"https://assets.coingecko.com/coins/images/4128/large/solana.png?1640133422\",\"current_price\":20.13,\"market_cap\":7813374350,\"market_cap_rank\":11,\"fully_diluted_valuation\":10973534309,\"total_volume\":226721619,\"high_24h\":20.3,\"low_24h\":19.95,\"price_change_24h\":-0.16829522583343248,\"price_change_percentage_24h\":-0.8291,\"market_cap_change_24h\":-49635087.72604656,\"market_cap_change_percentage_24h\":-0.63125,\"circulating_supply\":387939283.320986,\"total_supply\":544843347.413207,\"max_supply\":null,\"ath\":259.96,\"ath_change_percentage\":-92.25632,\"ath_date\":\"2021-11-06T21:54:35.825Z\",\"atl\":0.500801,\"atl_change_percentage\":3919.64345,\"atl_date\":\"2020-05-11T19:35:23.449Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:52.931Z\"},{\"id\":\"polkadot\",\"symbol\":\"dot\",\"name\":\"Polkadot\",\"image\":\"https://assets.coingecko.com/coins/images/12171/large/polkadot.png?1639712644\",\"current_price\":6.13,\"market_cap\":7487875989,\"market_cap_rank\":12,\"fully_diluted_valuation\":7984642346,\"total_volume\":135534333,\"high_24h\":6.24,\"low_24h\":6.11,\"price_change_24h\":-0.04892738674312547,\"price_change_percentage_24h\":-0.79128,\"market_cap_change_24h\":-39909470.58534622,\"market_cap_change_percentage_24h\":-0.53016,\"circulating_supply\":1220089527.23401,\"total_supply\":1301033633.49881,\"max_supply\":null,\"ath\":54.98,\"ath_change_percentage\":-88.84222,\"ath_date\":\"2021-11-04T14:10:09.301Z\",\"atl\":2.7,\"atl_change_percentage\":127.42361,\"atl_date\":\"2020-08-20T05:48:11.359Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:23:00.210Z\"},{\"id\":\"binance-usd\",\"symbol\":\"busd\",\"name\":\"Binance USD\",\"image\":\"https://assets.coingecko.com/coins/images/9576/large/BUSD.png?1568947766\",\"current_price\":1.001,\"market_cap\":7081680401,\"market_cap_rank\":13,\"fully_diluted_valuation\":7081680401,\"total_volume\":2385806102,\"high_24h\":1.003,\"low_24h\":0.999703,\"price_change_24h\":-0.00029931691350682,\"price_change_percentage_24h\":-0.02989,\"market_cap_change_24h\":8227294,\"market_cap_change_percentage_24h\":0.11631,\"circulating_supply\":7070499364.22,\"total_supply\":7070499364.22,\"max_supply\":null,\"ath\":1.15,\"ath_change_percentage\":-13.2744,\"ath_date\":\"2020-03-13T02:35:42.953Z\",\"atl\":0.901127,\"atl_change_percentage\":11.08202,\"atl_date\":\"2021-05-19T13:04:37.445Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:53.078Z\"},{\"id\":\"litecoin\",\"symbol\":\"ltc\",\"name\":\"Litecoin\",\"image\":\"https://assets.coingecko.com/coins/images/2/large/litecoin.png?1547033580\",\"current_price\":89.73,\"market_cap\":6523222153,\"market_cap_rank\":14,\"fully_diluted_valuation\":7540302785,\"total_volume\":337202213,\"high_24h\":90.96,\"low_24h\":89.29,\"price_change_24h\":-0.506554955377851,\"price_change_percentage_24h\":-0.56138,\"market_cap_change_24h\":-25586331.09127426,\"market_cap_change_percentage_24h\":-0.3907,\"circulating_supply\":72669583.2334713,\"total_supply\":84000000.0,\"max_supply\":84000000.0,\"ath\":410.26,\"ath_change_percentage\":-78.12016,\"ath_date\":\"2021-05-10T03:13:07.904Z\",\"atl\":1.15,\"atl_change_percentage\":7713.44626,\"atl_date\":\"2015-01-14T00:00:00.000Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:23:00.792Z\"},{\"id\":\"shiba-inu\",\"symbol\":\"shib\",\"name\":\"Shiba Inu\",\"image\":\"https://assets.coingecko.com/coins/images/11939/large/shiba.png?1622619446\",\"current_price\":1.091e-05,\"market_cap\":6434539766,\"market_cap_rank\":15,\"fully_diluted_valuation\":null,\"total_volume\":158988879,\"high_24h\":1.099e-05,\"low_24h\":1.084e-05,\"price_change_24h\":-2.8288594917e-08,\"price_change_percentage_24h\":-0.2585,\"market_cap_change_24h\":-8069411.731411934,\"market_cap_change_percentage_24h\":-0.12525,\"circulating_supply\":589358140254560.9,\"total_supply\":999990888207043.0,\"max_supply\":null,\"ath\":8.616e-05,\"ath_change_percentage\":-87.33193,\"ath_date\":\"2021-10-28T03:54:55.568Z\",\"atl\":5.6366e-11,\"atl_change_percentage\":19363672.84501,\"atl_date\":\"2020-11-28T11:26:25.838Z\",\"roi\":null,\"last_updated\":\"2023-04-09T18:22:56.627Z\"}]"

interface ValuteApi {
    @GET("coins/markets")
    suspend fun getCurrencyList(
        @Query("vs_currency") vs_currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") per_page: String = "100",
        @Query("locale") locale: String = "en",
    ): List<CurrencyRecyclerViewAdapter.ItemModel>
}