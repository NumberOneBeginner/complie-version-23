package com.none.staff.domain;

import java.io.Serializable;

import com.google.gson.Gson;

/***
 * 发送声波的bean
 * @author willis
 *
 */
public class StartGame implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private String amount ;
	private String bonusAmount ;
	private String bonusMoney ;
	private String bossRequest ;
	private String currency ;
	private String defaultMoney ;
	private String flag_hkd ;
	private String flag_rmb ; 
	private String gameDate ;
	private String gameId ;
	private String gameTime ;
	private String location ;
	private String sponsorId ;
	private String notEnough ;
	private String soundPlay ;
	private String staffRequest ;
	
	
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBonusAmount() {
		return bonusAmount;
	}
	public void setBonusAmount(String bonusAmount) {
		this.bonusAmount = bonusAmount;
	}
	public String getBonusMoney() {
		return bonusMoney;
	}
	public void setBonusMoney(String bonusMoney) {
		this.bonusMoney = bonusMoney;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDefaultMoney() {
		return defaultMoney;
	}
	public void setDefaultMoney(String defaultMoney) {
		this.defaultMoney = defaultMoney;
	}
	public String getFlag_hkd() {
		return flag_hkd;
	}
	public void setFlag_hkd(String flag_hkd) {
		this.flag_hkd = flag_hkd;
	}
	public String getFlag_rmb() {
		return flag_rmb;
	}
	public void setFlag_rmb(String flag_rmb) {
		this.flag_rmb = flag_rmb;
	}
	public String getGameDate() {
		return gameDate;
	}
	public void setGameDate(String gameDate) {
		this.gameDate = gameDate;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public String getGameTime() {
		return gameTime;
	}
	public void setGameTime(String gameTime) {
		this.gameTime = gameTime;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSponsorId() {
		return sponsorId;
	}
	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}
	public String getNotEnough() {
		return notEnough;
	}
	public void setNotEnough(String notEnough) {
		this.notEnough = notEnough;
	}
	
	
	
	public String getBossRequest() {
		return bossRequest;
	}
	public void setBossRequest(String bossRequest) {
		this.bossRequest = bossRequest;
	}
	public String getSoundPlay() {
		return soundPlay;
	}
	public void setSoundPlay(String soundPlay) {
		this.soundPlay = soundPlay;
	}
	public String getStaffRequest() {
		return staffRequest;
	}
	public void setStaffRequest(String staffRequest) {
		this.staffRequest = staffRequest;
	}
	
	
	@Override
	public String toString() {
		return "StartGame [amount=" + amount + ", bonusAmount=" + bonusAmount
				+ ", bonusMoney=" + bonusMoney + ", bossRequest=" + bossRequest
				+ ", currency=" + currency + ", defaultMoney=" + defaultMoney
				+ ", flag_hkd=" + flag_hkd + ", flag_rmb=" + flag_rmb
				+ ", gameDate=" + gameDate + ", gameId=" + gameId
				+ ", gameTime=" + gameTime + ", location=" + location
				+ ", sponsorId=" + sponsorId + ", notEnough=" + notEnough
				+ ", soundPlay=" + soundPlay + ", staffRequest=" + staffRequest
				+ "]";
	}
	public static StartGame  parse(String jsonString){
		Gson gson = new Gson() ;
		return gson.fromJson(jsonString, StartGame.class) ;
	}

	
}
