package com.htetarkarzaw.printer

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pos.printer.PrinterFunctions
import com.pos.printer.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var portName = "/dev/ttyACM1"
    var portSettings = 115200
    private var deviceId=""
    private var deviceName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val androidId = Settings.Secure.getString(contentResolver,
                Settings.Secure.ANDROID_ID)
            deviceId = androidId
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        } catch (e: Exception) {
            Log.e("DeviceIdException", e.localizedMessage ?: "")
        }
        btnConnect.setOnClickListener {
            if(PrinterFunctions.OpenPort2(portName, portSettings)==0){
                tvStatus.text = "Success"
            }else{
                tvStatus.text = "Fail"
            }
        }
        btnPrint.setOnClickListener {
            var textData = "Hello World Hello World Hello Wo"
            textData += "\\n"
            textData += "Hello World Hello World Hello Wo"
            textData += "\\n"
            var data = "           My Store1            \n" +
                    "U Kyaw Hla St, Yangon, Myanmar (Burma)\n" +
                    "Date            : 12-08-2020\n" +
                    "Time            : 04:24:47 pm\n" +
                    "Transaction ID  : 84398283549858\n" +
                    "Cashier         : Hnin\n" +
                    "Payment Type    : Cash\n" +
                    "--------------------------------\n" +
                    "Items               Qty    Price\n" +
                    "--------------------------------\n" +
                    "Alpine water          1      200\n" +
                    "--------------------------------\n" +
                    "Total                 MMK 200.00\n" +
                    "--------------------------------\n" +
                    "Paid Amount           MMK 200.00\n" +
                    "           Thank You!           \n" +
                    "       Please Come Again...      \n" +
                    "\n"
            printPosiflexText(data)
        }
    }

    private fun printPosiflexText(textData: String) {
        PrinterFunctions.PrintText(
            portName,
            portSettings,
            0, 0, 0, 0, 0, 0, 0, 0,
            textData);
        PrinterFunctions.PrintBarCode(portName,portSettings,"57638176079989" +
                "",0,162,4,0,69)
        PrinterFunctions.PreformCut(portName, portSettings,1);
        PrinterFunctions.PreformCut(portName, portSettings,1);
        PrinterFunctions.PreformCut(portName, portSettings,1);
    }

    fun getPrintingString(
        storeName: String,
        storeAddress: String,
        date: String,
        time: String,
        transactionId: String,
        cashier: String,
        paymentType: String,
        total: String,
        discount: String,
        paidAmount: String,
        changeAmount: String,
        currencyType: String,
        itemList: List<SaleReportDetailStockVO>): String {
        var printingString = ""
        printingString += getWrappedCenterText(storeName)
        printingString += "\n"
        printingString += getWrappedCenterText(storeAddress)
        printingString += "\n"
        printingString += "Date            : ${date}"
        printingString += "\n"
        printingString += "Time            : ${time}"
        printingString += "\n"
        printingString += "Transaction ID  : ${transactionId}"
        printingString += "\n"
        printingString += "Cashier         : ${cashier}"
        printingString += "\n"
        printingString += "Payment Type    : ${paymentType}"
        printingString += "\n"
        printingString += "------------------------------------------------"
        printingString += getItemTableWrappedText(itemList)
        printingString += "------------------------------------------------"
        printingString += getBottomText(currencyType, total, discount, paidAmount, changeAmount)
        printingString += "\n"
        printingString += getWrappedCenterText("Thank You!")
        printingString += "\n"
        printingString += getWrappedCenterText("Please Come Again...")
        printingString += "\n"
        printingString += "\n"

        return printingString
    }

    fun getWrappedCenterText(value: String): String? {
        // Initialization
        val formattedText = StringBuilder()

        // Maximum number of string length for 80mm Thermal Printer Paper.
        val max = 48

        // Brand Main Title
        val leftCount = max - value.length
        val medium = leftCount / 2

        for (i in 0 until medium) {
            formattedText.append(" ")
        }

        formattedText.append(value)

        for (i in 0 until leftCount - medium) {
            formattedText.append(" ")
        }

        return formattedText.toString()
    }

    fun getItemTableWrappedText(itemList: List<SaleReportDetailStockVO>): String? {
        // Initialization
        // StringBuilder to be return.
        val formattedText = StringBuilder()
        // Maximum number of string length for 80mm Thermal Printer Paper.
        val max = 48
        // Maximum number of string length for each column
        val maxItemCount = 28
        val maxPriceCount = 15
        val maxQtyCount = 5
        // Divider
        val divider = "------------------------------------------------"

        formattedText.append("\n")

        // Item Title
        val item = "Items"
        formattedText.append(item)
        for (i in 0 until maxItemCount - item.length) {
            formattedText.append(" ")
        }

        // Quantity Title
        val qty = "Qty"
        for (i in 0 until maxQtyCount - qty.length) {
            formattedText.append(" ")
        }
        formattedText.append(qty)

        // Price Title
        val price = "Price"
        for (i in 0 until maxPriceCount - price.length) {
            formattedText.append(" ")
        }
        formattedText.append(price)

        formattedText.append("\n")
        formattedText.append(divider.toString())
        formattedText.append("\n")

        // Make sure SaleOrderLine list is null?
        if (itemList.size > 0) {

            // Manipulate rows according to SaleOrderLine items
            /**for (item in itemList) {
            var nextItemName: String
            var splittedItemName: String
            val itemNameList: ArrayList<String> = ArrayList(0)

            val itemName = "Alpine Alpine Alpine Alpine Alpine Alpine Alpine Alpine"
            val itemPrice = currencyType + item.price
            val itemQuantity = item.quantity

            if(itemName!!.length > 24) {
            nextItemName = itemName.substring(0, 24) // "Lorem" <- "Lorem Ipsm Kohe loran"
            splittedItemName = itemName.substring(24, (itemName.length - 1)) // " Ipsm Kohe loran" <- "Lorem Ipsm Kohe loran"

            formattedText.append(String.format("%-24s%13s%11s\n", nextItemName, currencyType + itemPrice, itemQuantity))

            while(splittedItemName.length > 24) {
            nextItemName = splittedItemName.substring(0, 24) // " Ipsm"
            splittedItemName = splittedItemName.substring(24, (itemName.length - 1)) // " Kohe loran" <- " Ipsm Kohe loran"
            itemNameList.add(nextItemName)
            }

            for(item in itemNameList) {
            formattedText.append(String.format("%-24s%13s%11s\n", item, "", ""))
            }
            } else {
            formattedText.append(String.format("%-24s%13s%11s\n", itemName, itemPrice, itemQuantity))
            }*/

            for (item in itemList) {
                // Name
                var itemName = ""

                if(item.name?.length!! > 28) {
                    itemName = item.name!!.substring(0, 28)
                } else {
                    itemName = item.name!!
                }

                if (itemName.length > maxItemCount) {
                    val avail = CharArray(maxItemCount)
                    for (i in 0 until maxItemCount) {
                        avail[i] = itemName[i]
                    }
                    var extra: CharArray
                    val tmpExtra = StringBuilder()
                    for (i in 0 until itemName.length) {
                        if (i > maxItemCount - 1) {
                            tmpExtra.append(itemName[i])
                        }
                    }
                    extra = tmpExtra.toString().toCharArray()
                    for (c in avail) {
                        formattedText.append(c)
                    }
                    formattedText.append("\n")
                    for (c in extra) {
                        formattedText.append(c)
                    }
                    for (i in 0 until maxItemCount - extra.size) {
                        formattedText.append(" ")
                    }
                } else {
                    formattedText.append(itemName)
                    for (i in 0 until maxItemCount - itemName.length) {
                        formattedText.append(" ")
                    }
                }

                // Qty
                val itemQty: String = item.quantity!!
                for (i in 0 until maxQtyCount - itemQty.length) {
                    formattedText.append(" ")
                }
                formattedText.append(itemQty)

                // Price
                val itemPrice: String = item.price!!
                for (i in 0 until maxPriceCount - itemPrice.length) {
                    formattedText.append(" ")
                }
                formattedText.append(itemPrice)

                formattedText.append("\n")
            }
        }

        return formattedText.toString()
    }

    fun getBottomText(currency: String, total: String, discount: String, paidAmount: String, changeAmount: String): String {
        // Initialization
        // StringBuilder to be return.
        val formattedText = StringBuilder()
        // Maximum number of string length for 80mm Thermal Printer Paper.
        val max = 48
        // Maximum number of string length for each column
        val maxFrontCount = 24
        val maxBackCount = 24
        // Divider
        val divider = "------------------------------------------------"

        // New Line
        formattedText.append("\n")

        // Total
        val totalLabel = "Total"
        formattedText.append(totalLabel)
        for (i in 0 until maxFrontCount - totalLabel.length) {
            formattedText.append(" ")
        }
        for (i in 0 until maxBackCount - (currency.length + total.length)) {
            formattedText.append(" ")
        }
        formattedText.append(currency + total)

        // New Line
        formattedText.append("\n")
        formattedText.append(divider)
        formattedText.append("\n")

        // Discount
        if(discount != "0.00") {
            val discountLabel = "Discount"
            formattedText.append(discountLabel)
            for (i in 0 until maxFrontCount - discountLabel.length) {
                formattedText.append(" ")
            }
            for (i in 0 until maxBackCount - (currency.length + discount.length)) {
                formattedText.append(" ")
            }
            formattedText.append(currency + discount)
        }

        // Paid Amount
        val paidAmountLabel = "Paid Amount"
        formattedText.append(paidAmountLabel)
        for (i in 0 until maxFrontCount - paidAmountLabel.length) {
            formattedText.append(" ")
        }
        for (i in 0 until maxBackCount - (currency.length + paidAmount.length)) {
            formattedText.append(" ")
        }
        formattedText.append(currency + paidAmount)

        // Change Amount
        if(changeAmount != "0.00") {
            val changeAmountLabel = "Change Amount"
            formattedText.append(changeAmountLabel)
            for (i in 0 until maxFrontCount - changeAmountLabel.length) {
                formattedText.append(" ")
            }
            for (i in 0 until maxBackCount - (currency.length + changeAmount.length)) {
                formattedText.append(" ")
            }
            formattedText.append(currency + changeAmount)
        }

        return formattedText.toString()
    }
}