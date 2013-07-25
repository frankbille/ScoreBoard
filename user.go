package scoreboard

type UserInfo struct {
	LoggedIn       bool   `json:"loggedIn"`
	UserID         string `json:"userId"`
	LoginLogoutUrl string `json:"loginLogoutUrl"`
	Email          string `json:"email"`
}
