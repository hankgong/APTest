package com.example.APTest;

/**
 * Created with IntelliJ IDEA.
 * User: hgong
 * Date: 23/05/13
 * Time: 5:06 PM
 * WiFi connect wrapper found online
 * http://blog.csdn.net/ajq1989/article/details/8891516
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiConnect {
    WifiManager mWifiManager;

    /**
     * enum types of password encryption algorithm
     */
    public enum WifiCipherType
    {
        WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiConnect(WifiManager wifiManager)
    {
        this.mWifiManager = wifiManager;
    }

    private boolean openWifi()
    {
        boolean ret = true;
        if (!mWifiManager.isWifiEnabled())
        {
            ret = mWifiManager.setWifiEnabled(true);
        }
        return ret;
    }

    /**
     * Main method provided for public use
     * @param SSID
     * @param password
     * @param type
     * @return
     */
    public boolean connect(String SSID, String password, WifiCipherType type)
    {
        if(!this.openWifi()){
            return false;           //wifi can't be enabled
        }

        Log.i("WiFi", "Enabling interface...");
        //It will take around 1-3 seconds to enable wifi, so use thread delay to check status WIFI_STATE_ENABLING
        // until it works
        while(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING ) {
            try {
                //checking with interval to avoid blocking the UI
                Thread.currentThread();
                Thread.sleep(100);
            } catch(InterruptedException ie) {
                //nothing to do for now
            }
        }

        WifiConfiguration wifiConfig = this.createWifiInfo(SSID, password, type);

        if(wifiConfig == null) {
            return false;
        }

        WifiConfiguration tempConfig = this.isExsits(SSID);

        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        //static ip address setting
        //try {
        //    String ip  ="192.168.1.201";
        //    int networkPrefixLength =24;
        //    InetAddress intetAddress  = InetAddress.getByName(ip);
        //    int intIp = inetAddressToInt(intetAddress);
        //    String dns = (intIp & 0xFF ) + "." + ((intIp >> 8 ) & 0xFF) + "." + ((intIp >> 16 ) & 0xFF) + ".1";
        //    setIpAssignment("STATIC", wifiConfig); //"STATIC" or "DHCP" for dynamic setting
        //    setIpAddress(intetAddress, networkPrefixLength, wifiConfig);
        //    setGateway(InetAddress.getByName(dns), wifiConfig);
        //    setDNS(InetAddress.getByName(dns), wifiConfig);
        //} catch (Exception e) {
        //    // TODO: handle exception
        //    e.printStackTrace();
        //}

        //don't care about previous saving, remove it and then add it again
        Log.i("WiFi", "doing the association");
        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean bRet = mWifiManager.enableNetwork(netID, true);

        if (bRet)
            mWifiManager.saveConfiguration();

        return bRet;
    }

    //check if the network is already saved in config file
    private WifiConfiguration isExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\""+SSID+"\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String password, WifiCipherType type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        if(type == WifiCipherType.WIFICIPHER_NOPASS){
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        if(type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }

        if(type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }


    /***
     * Convert a IPv4 address from an InetAddress to an integer
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr)
            throws IllegalArgumentException {
        byte [] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) |
                ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    public static void setIpAddress(InetAddress addr, int prefixLength,WifiConfiguration wifiConf) throws SecurityException,IllegalArgumentException,
            NoSuchFieldException,IllegalAccessException, NoSuchMethodException,ClassNotFoundException, InstantiationException,InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[] {InetAddress.class, int.class });
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,"mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway,WifiConfiguration wifiConf) throws SecurityException,IllegalArgumentException,
            NoSuchFieldException,IllegalAccessException, ClassNotFoundException,NoSuchMethodException, InstantiationException,InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[] { InetAddress.class });
        Object routeInfo = routeInfoConstructor.newInstance(gateway);
        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties,"mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException,IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

//	    public void editStaticWifiConfig(final ScanResult sr,String pwd, String ip, String gateway,int prefixLength,String dns) throws Exception{
//	    	WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);
//
//	    	if(historyWifiConfig == null){
//	    		historyWifiConfig = createComWifiConfig(sr.SSID,pwd);
//	    		int netId = mWifiManager.addNetwork(historyWifiConfig);
//	    		mWifiManager.enableNetwork(netId, true);
//	    	}
//
//	        setIpAssignment("STATIC", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
//	        setIpAddress(InetAddress.getByName(ip), prefixLength, historyWifiConfig);
//	        setGateway(InetAddress.getByName(gateway), historyWifiConfig);
//	        setDNS(InetAddress.getByName(dns), historyWifiConfig);
//
//	        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
//		}
//
//	    public void editDhcpWifiConfig(final ScanResult sr,String pwd) throws Exception{
//	    	WifiConfiguration historyWifiConfig = getHistoryWifiConfig(sr.SSID);
//
//	    	if(historyWifiConfig == null){
//	    		historyWifiConfig = createComWifiConfig(sr.SSID,pwd);
//	    		int netId = mWifiManager.addNetwork(historyWifiConfig);
//	    		mWifiManager.enableNetwork(netId, true);
//	    	}
//
//	        setIpAssignment("DHCP", historyWifiConfig); //"STATIC" or "DHCP" for dynamic setting
//
//	        mWifiManager.updateNetwork(historyWifiConfig); //apply the setting
//		}
}
