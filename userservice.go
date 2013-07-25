package scoreboard

import (
	"appengine/user"
)

func (r ScoreBoardService) HandleGetUserInfo() UserInfo {
	c := GetContext(r.Request())

	userInfo := UserInfo{}
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		userInfo.LoggedIn = false
		userInfo.LoginLogoutUrl = url
	} else {
		url, _ := user.LogoutURL(c, "/")
		userInfo.LoggedIn = true
		userInfo.LoginLogoutUrl = url
		userInfo.Email = u.Email
		userInfo.UserID = u.ID
	}
	
	return userInfo
}
