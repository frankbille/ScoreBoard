package restapi

import (
    "appengine"
)

func (r ScoreBoardService) HandleImportOldVersion() string {
    c := appengine.NewContext(r.Request())

    return ""
}
