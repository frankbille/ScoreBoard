package scoreboard

import (
	"appengine/user"
	"github.com/emicklei/go-restful"
)

type UserService struct {
}

func (us UserService) GetUserInfo(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)

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

	response.WriteEntity(userInfo)
}
