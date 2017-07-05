package com.teammerge.utils;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.ExtCommitModel;
import com.teammerge.model.TicketCommitsModel;

public class JacksonUtils {
  public static String toJson(List<ActivityModel> activities) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activities);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }


  public static String toTicketCommitsJson(List<ExtCommitModel> commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }
}
