package scoreboard

func (r ScoreBoardService) HandleGetAllPlayers() []Player {
	c := GetContext(r.Request())
	return LoadAllPlayers(c)
}
