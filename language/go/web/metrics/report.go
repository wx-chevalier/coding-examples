package metrics

import (
	"net/http"

	gometrics "github.com/rcrowley/go-metrics"
)

func ReportHandler(w http.ResponseWriter, r *http.Request) {

	w.WriteHeader(http.StatusOK)

	t := gometrics.GetOrRegisterTimer("reporthandler.writemetrics", nil)
	t.Time(func() {
		gometrics.WriteJSONOnce(gometrics.DefaultRegistry, w)
	})
}
