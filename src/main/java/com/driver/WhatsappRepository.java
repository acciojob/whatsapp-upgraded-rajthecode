package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepository {
    HashMap<String,User> userMap = new HashMap<>();
    HashMap<Group, List<Message>> GMmap = new HashMap<>();
    HashMap<User,List<Message>> UMmap = new HashMap<>();
    HashMap<Group,List<User>> GUmap = new HashMap<>();
    List<Message> messageList = new ArrayList<>();

    public int count = 1;
    public int messageCount = 0;
    public String createUser(String name,String mobile) throws Exception{
        if(userMap.containsKey(mobile))
            throw new Exception("User already exists");
        else{
            User user = new User(name,mobile);
            userMap.put(mobile,user);
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User>users){
        Group group = new Group();

        if(users.size()==2){
            group.setName(users.get(1).getName());
            group.setNumberOfParticipants(2);
        }
        else {
            group.setName("Group "+count);
            count++;
            group.setNumberOfParticipants(users.size());
        }
        GUmap.put(group,users);
        return group;
    }

    public int createMessage(String content){
        Message message = new Message();
        message.setId(messageCount++);
        message.setTimestamp(new Date());
        message.setContent(content);
        messageList.add(message);
        return messageCount;
    }

    public int sendMessage(Message message, User sender, Group group)throws Exception{
        boolean userCheck=false;
        if(!GUmap.containsKey(group))
            throw new Exception("Group does not exist");

        List<User> users = GUmap.get(group);
        for(User user:users){
            if(user==sender){
                userCheck=true;
                break;
            }
        }
        if(userCheck==false)
            throw new Exception("You are not allowed to send message");

        if(GMmap.containsKey(group)){
            GMmap.get(group).add(message);
        }else {
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            GMmap.put(group,messages);
        }

        if(UMmap.containsKey(sender)){
            UMmap.get(sender).add(message);
        }else {
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            UMmap.put(sender,messages);
        }
        return GMmap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!GUmap.containsKey(group))
            throw new Exception("Group does not exist");
        if(GUmap.get(group).get(0)!=approver)
            throw new Exception("Approver does not have rights");

        boolean cheakUser = false;
        int index = -1;

        for(User user1:GUmap.get(group)){
            if(user1==user){
                cheakUser=true;
                index = GUmap.get(group).indexOf(user1);
                break;
            }
        }

        if(cheakUser==false)
            throw new Exception("User is not a participant");

        User oldAdmin = GUmap.get(group).get(0);
        GUmap.get(group).add(0,user);
        GUmap.get(group).add(index,oldAdmin);

        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        Group group1 = null;
        boolean ckeckUser = false;

        for(Group group: GUmap.keySet()){
            for(User user1: GUmap.get(group)){
                if(user1==user){
                    group1 = group;
                    ckeckUser = true;
                    break;
                }
            }
            if(group1!=null)
                break;
        }

        if(ckeckUser==false)
            throw new Exception("User not found");

        if(GUmap.get(group1).get(0)==user)
            throw new Exception("Cannot remove admin");

        List<Message> messages = UMmap.get(user);

        for(Message message:GMmap.get(group1)){
            if(messages.contains(message))
                GMmap.get(group1).remove(message);
        }

        for(Message message:messageList){
            if(messages.contains(message))
                messageList.remove(message);
        }

        GUmap.get(group1).remove(user);
        UMmap.remove(user);

        return GUmap.get(group1).size()+GMmap.get(group1).size()+messageList.size();
    }

//    public String findMessage(Date start, Date end, int K) throws Exception{
//
//    }
}