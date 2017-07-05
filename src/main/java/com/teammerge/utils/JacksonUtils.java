package com.teammerge.utils;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teammerge.model.ActivityModel;

public class JacksonUtils {
  public static String convertActivitiestoJson(List<ActivityModel> activities) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activities);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }
}
