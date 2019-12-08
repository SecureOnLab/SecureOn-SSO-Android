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
	
	public String getPageURL() {
		return pageURL;
	}
	public void setPageURL(String pageURL) {
		SampleVO.pageURL = pageURL;
	}
	public String getTokenKey() {
		return tokenKey;
	}
	public void setTokenKey(String tokenKey) {
		SampleVO.tokenKey = tokenKey;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		SampleVO.token = token;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		SampleVO.clientIp = clientIp;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		SampleVO.userId = userId;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		SampleVO.userPwd = userPwd;
	}
	public byte[] getSecId() {
		return secId;
	}
	public void setSecId(byte[] secId) {
		SampleVO.secId = secId;
	}
	public int getSsoErrorCode() {
		return ssoErrorCode;
	}
	public void setSsoErrorCode(int ssoErrorCode) {
		SampleVO.ssoErrorCode = ssoErrorCode;
	}
	public int getNetworkStatus() {
		return networkStatus;
	}
	public void setNetworkStatus(int networkStatus) {
		SampleVO.networkStatus = networkStatus;
	}
}
