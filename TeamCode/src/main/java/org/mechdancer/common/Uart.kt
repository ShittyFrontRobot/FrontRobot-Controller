package org.mechdancer.common

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import cn.wch.ch34xuartdriver.CH34xUARTDriver

/**
 * CH34x Uart 串口通信
 */
class Uart(context: Context) {

    companion object {
        const val ACTION_USB_PERMISSION = "org.mechdancer.uartdemo.USB_PERMISSION"
    }

    val driver =
        CH34xUARTDriver(
            context.getSystemService(Context.USB_SERVICE) as UsbManager,
            context,
            ACTION_USB_PERMISSION)

    /**
     * 返回设备 USB 是否可用
     */
    val isSupported
        get() = driver.UsbFeatureSupported()

    /**
     * 返回已连接的设备或空
     */
    fun getDevice(): UsbDevice? = driver.EnumerateDevice()

    /**
     * 获取检验 USB 权限
     *
     * @return
     *          0 权限已获取
     *         -1 未找到设备
     *         -2 准备获取权限
     */
    fun requestPermission() =
        driver.ResumeUsbPermission()


    /**
     * 找到并开启设备
     *
     * @return
     *          0 开启成功
     *         -1 未找到设备
     */
    fun getAndOpenDevice() =
        driver.ResumeUsbList()


    /**
     * 返回设备连接或空
     */
    fun getConnection(): UsbDeviceConnection? =
        driver.mDeviceConnection

    /**
     * 写入 [byteArray]
     */
    fun write(byteArray: ByteArray) =
        driver.WriteData(byteArray, byteArray.size)


    /**
     * 读取 [size] 字节
     */
    fun read(size: Int = 4096): ByteArray? {
        val byteArray = ByteArray(size)
        val n = driver.ReadData(byteArray, size)
        return when {
            n == 0   -> null
            n < size -> byteArray.dropLast(size - n).toByteArray()
            else     -> byteArray
        }
    }

    /**
     * 配置串口
     *
     * @return 是否成功
     */
    fun setConfig(config: Config): Boolean =
        with(config) {
            driver.SetTimeOut(timeout, timeout)
            driver.SetConfig(baudRate.value, bits, stopByte, parity, flowControl)
        }


    /**
     * 关闭设备
     */
    fun close() = driver.CloseDevice()

    /**
     * 一步到位
     */
    fun init(config: Config) {
        require(isSupported)
        require(requestPermission() == 0)
        require(getAndOpenDevice() == 0)
        require(getConnection() != null)
        require(driver.UartInit())
        require(setConfig(config))
    }

    /**
     * 尝试初始化设备
     *
     * @return 是否成功
     */
    fun initUartDevice() = driver.UartInit()

    /**
     * 波特率
     */
    @Suppress("EnumEntryName")
    enum class BaudRate {
        _300,
        _600,
        _1200,
        _2400,
        _4800,
        _9600,
        _19200,
        _38400,
        _57600,
        _115200,
        _230400,
        _460800,
        _921600;

        val value by lazy { name.removePrefix("_").toInt() }

    }

    /**
     * 串口设备配置
     */
    data class Config(
        /**
         * 波特率，时钟频率
         */
        val baudRate: BaudRate = BaudRate._9600,
        /**
         * 数据位，每个字符的位数
         */
        val bits: Byte = 8,
        /**
         * 停止位的数量，可为 1 或 2
         */
        val stopByte: Byte = 1,
        /**
         * 奇偶校验 0 无 1 奇 2 偶
         */
        val parity: Byte = 0,
        /**
         * 流控制类型 0 无 1 cts/rts
         */
        val flowControl: Byte = 0,
        /**
         * 读写超时 (ms)
         */
        val timeout: Int = 10000
    )
}