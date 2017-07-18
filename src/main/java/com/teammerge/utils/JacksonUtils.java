package com.teammerge.utils;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.BranchModel;
import com.teammerge.model.CommitModel;

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


  public static String toTicketCommitsJson(List<CommitModel> commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }

  public static String toCommitsDetailJson(List<CommitDetailModel> commits) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(commits);
    } catch (JsonProcessingException e) {
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
    }

    return jsonInString;
  }

  public static String toBrachNamesJson(List<BranchModel> branchs) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(branchs);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }



  public static String toBranchDetailJson(BranchDetailModel branchDetailModel) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(branchDetailModel);
    } catch (JsonProcessingException e) {
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
    }

    return jsonInString;
  }

  public static String toCompanyDetailJson(Company company) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(company);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonInString;
  }

  public static String toCredentialDetailJson(RepoCredentials credential) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credential);
    } catch (JsonProcessingException e) {
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
    }

    return jsonInString;
  }
}
