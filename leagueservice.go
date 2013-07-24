package scoreboard

func (r ScoreBoardService) HandleGetAllLeagues() []League {
	c := GetContext(r.Request())
	return LoadAllLeagues(c)
}
