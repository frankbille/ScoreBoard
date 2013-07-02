package restapi

import (
    "appengine"
    "appengine/datastore"
    "encoding/xml"
    "fmt"
    "io/ioutil"
    "net/http"
    "restapi/domain"
    "strconv"
    "strings"
)

type Field struct {
    Name  string `xml:"name,attr"`
    Value string `xml:",innerxml"`
}

type Row struct {
    Fields []Field `xml:"field"`
}

type TableData struct {
    Name string `xml:"name,attr"`
    Rows []Row  `xml:"row"`
}

type Database struct {
    Tables []TableData `xml:"table_data"`
}

type MysqlDump struct {
    Databases []Database `xml:"database"`
}

func importOldVersion(w http.ResponseWriter, r *http.Request) {
    c := appengine.NewContext(r)

    file, _, err := r.FormFile("xmlDumpFile")

    if err != nil {
        w.WriteHeader(http.StatusInternalServerError)
        fmt.Fprintf(w, "error: %v\n", err)
        return
    }

    data, err := ioutil.ReadAll(file)

    fmt.Fprintf(w, "Length of data: %v\n", len(data))
    // fmt.Fprintf(w, "Data:\n%v\n", string(data))

    if err != nil {
        w.WriteHeader(http.StatusInternalServerError)
        fmt.Fprintf(w, "error: %v\n", err)
        return
    }

    v := MysqlDump{}

    err = xml.Unmarshal(data, &v)

    if err != nil {
        w.WriteHeader(http.StatusInternalServerError)
        fmt.Fprintf(w, "error: %v\n", err)
        return
    }

    if len(v.Databases) != 1 {
        w.WriteHeader(http.StatusInternalServerError)
        fmt.Fprintf(w, "Only one database must be present in the dump file")
        return
    }

    tables := v.Databases[0].Tables

    for i := 0; i < len(tables); i++ {
        table := tables[i]
        // Players
        if strings.EqualFold(table.Name, "player") {
            for _, row := range table.Rows {
                var Name, FullName, GroupName string
                for _, field := range row.Fields {
                    if field.Name == "name" {
                        Name = field.Value
                    } else if field.Name == "full_name" {
                        FullName = field.Value
                    } else if field.Name == "group_name" {
                        GroupName = field.Value
                    }
                }

                p := domain.NewPlayer(Name, FullName, GroupName)
                key := datastore.NewKey(c, "player", p.Id, 0, nil)
                key, err := datastore.Put(c, key, &p)

                if err != nil {
                    http.Error(w, err.Error(), http.StatusInternalServerError)
                    return
                }

                fmt.Fprintf(w, "Key: %v\n", key)
            }
        }
        // Leagues
        if strings.EqualFold(table.Name, "league") {
            for _, row := range table.Rows {
                var Name string
                var Active bool
                for _, field := range row.Fields {
                    if field.Name == "name" {
                        Name = field.Value
                    } else if field.Name == "active" {
                        Active, _ = strconv.ParseBool(field.Value)
                    }
                }

                l := domain.NewLeague(Name, Active)
                key := datastore.NewKey(c, "league", l.Id, 0, nil)
                key, err := datastore.Put(c, key, &l)

                if err != nil {
                    http.Error(w, err.Error(), http.StatusInternalServerError)
                    return
                }

                fmt.Fprintf(w, "Key: %v\n", key)
            }
        }
    }
}
