package com.teammerge.utils;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.model.BranchModel;
import com.teammerge.model.BranchModel;
import com.teammerge.model.CommitModel;

public class JacksonUtils {

  public static String toTicketCommitsJson(List<CommitModel> commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return jsonInString;
  }

  public static String toCommitsDetailJson(List<CommitModel> commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return jsonInString;
  }


  public static String toCommitsCountJson(int commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return jsonInString;
  }
  
  public static String toBranchCommitDetailJson(CommitModel commitModel) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commitModel);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return jsonInString;
  }

  public static String toJson(Object object) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return jsonInString;
  }
}
