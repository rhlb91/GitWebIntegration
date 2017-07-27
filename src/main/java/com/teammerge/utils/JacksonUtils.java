package com.teammerge.utils;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.model.BranchDetailModel;
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

  public static String toBranchDetailJson(BranchDetailModel branchDetailModel) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(branchDetailModel);
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

  public static String toAllCommitDetailJson(List<CommitModel> commitModel) {

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

  public static String toCompanyDetailJson(Company company) {

    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(company);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
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
