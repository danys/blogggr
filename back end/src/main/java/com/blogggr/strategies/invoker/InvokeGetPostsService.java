package com.blogggr.strategies.invoker;

import com.blogggr.dao.PostDAOImpl;
import com.blogggr.entities.Post;
import com.blogggr.exceptions.DBException;
import com.blogggr.exceptions.ResourceNotFoundException;
import com.blogggr.json.FilterFactory;
import com.blogggr.json.JsonFilter;
import com.blogggr.json.JsonTransformer;
import com.blogggr.services.PostService;
import com.blogggr.strategies.ServiceInvocationStrategy;
import com.blogggr.strategies.validators.GetPostsValidator;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Sunnen on 04.12.16.
 */
public class InvokeGetPostsService implements ServiceInvocationStrategy {

    private PostService postService;

    public InvokeGetPostsService(PostService postService){
        this.postService = postService;
    }

    public Object invokeService(Map<String,String> input, String body, Long userID) throws ResourceNotFoundException, DBException {
        //Check if the poster id is present
        String idStr = null;
        Long posterID = null;
        if (input.containsKey(GetPostsValidator.posterUserIDKey)) {
            idStr = input.get(GetPostsValidator.posterUserIDKey);
            try{
                long idp = Long.parseLong(idStr);
                posterID = new Long(idp);
            }
            catch(NumberFormatException e){
                //Will not be triggered: this is already checked in the validator
            }
        }
        //Check if titleKey is present
        String title = null;
        if (input.containsKey(GetPostsValidator.titleKey)){
            title = input.get(GetPostsValidator.titleKey);
        }
        //Check the visibility setting
        String visibility = null;
        PostDAOImpl.Visibility visi = PostDAOImpl.Visibility.all; //return all (from friends, global and current user) posts by default
        if (input.containsKey(GetPostsValidator.visibilityKey)) {
            visibility = input.get(GetPostsValidator.visibilityKey);
        }
        if (visibility!=null){
            if (visibility.compareTo("onlyGlobal")!=0) visi = PostDAOImpl.Visibility.onlyGlobal;
            else if (visibility.compareTo("all")!=0) visi = PostDAOImpl.Visibility.all;
            else if (visibility.compareTo("onlyFriends")!=0) visi = PostDAOImpl.Visibility.onlyFriends;
            else if (visibility.compareTo("onlyCurrentUser")!=0) visi = PostDAOImpl.Visibility.onlyCurrentUser;
        }
        else if (visibility==null) visi = PostDAOImpl.Visibility.all;
        List<Post> posts = postService.getPosts(userID,posterID,title,visi);
        //Filter attributes of the posts
        JsonNode node = JsonTransformer.filterFieldsOfMultiLevelObject(posts, FilterFactory.getPostFilter());
        return node;
    }
}
