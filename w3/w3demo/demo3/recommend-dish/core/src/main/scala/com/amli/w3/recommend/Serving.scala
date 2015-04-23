package com.amli.w3.recommend

import _root_.io.prediction.controller._

class Serving extends LServing[EmptyServingParams, Query, PredictedResult] {

  override def serve(query: Query, PredictedResults: Seq[PredictedResult]): PredictedResult = {
    PredictedResults.head
  }
}
