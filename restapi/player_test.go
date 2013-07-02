package scoreboard

import "testing"

func TestGetPlayerId(t *testing.T) {
    p := NewPlayer("Some Name", nil, nil)

    if p.GetId() != "some-name" {
        t.Errorf("Player.GetId() didn't return the correct slug like ID, based on the name. Expected %v, bus was %v", "some-name", p.GetId())
    }
}
