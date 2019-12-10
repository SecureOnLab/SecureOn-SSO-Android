package kr.co.secureon.sso.sample;

public class SampleVO {

	private static String pageURL;
	private static String tokenKey;
	private static String token;
	private static String clientIp;
	private static String userId;
	private static String userPwd;
	private static byte[] secId;
	private static int ssoErrorCode;
	private static int networkStatus;

	public static String getPageURL() {
		return pageURL;
	}

	public static void setPageURL(String pageURL) {
		SampleVO.pageURL = pageURL;
	}

	public static String getTokenKey() {
		return tokenKey;
	}

	public static void setTokenKey(String tokenKey) {
		SampleVO.tokenKey = tokenKey;
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		SampleVO.token = token;
	}

	public static String getClientIp() {
		return clientIp;
	}

	public static void setClientIp(String clientIp) {
		SampleVO.clientIp = clientIp;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		SampleVO.userId = userId;
	}

	public static String getUserPwd() {
		return userPwd;
	}

	public static void setUserPwd(String userPwd) {
		SampleVO.userPwd = userPwd;
	}

	public static byte[] getSecId() {
		return secId;
	}

	public static void setSecId(byte[] secId) {
		SampleVO.secId = secId;
	}

	public static int getSsoErrorCode() {
		return ssoErrorCode;
	}

	public static void setSsoErrorCode(int ssoErrorCode) {
		SampleVO.ssoErrorCode = ssoErrorCode;
	}

	public static int getNetworkStatus() {
		return networkStatus;
	}

	public static void setNetworkStatus(int networkStatus) {
		SampleVO.networkStatus = networkStatus;
	}
}
