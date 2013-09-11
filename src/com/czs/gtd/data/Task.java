package com.czs.gtd.data;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class Task implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Integer id; // 任务id
    private Integer type; // 任务类型
    private Integer priority; // 任务优先等级
    private Time time; // 提醒时间
    private Date date; // 任务日期
    private String content; // 任务内容
    private Integer finish; // 0表示未完成，1表示完成
    
    public Integer getFinish()
    {
        return this.finish;
    }
    
    public void setFinish(Integer finish)
    {
        this.finish = finish;
    }
    
    public Integer getId()
    {
        return this.id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public Integer getType()
    {
        return this.type;
    }
    
    public void setType(Integer type)
    {
        this.type = type;
    }
    
    public Integer getPriority()
    {
        return this.priority;
    }
    
    public void setPriority(Integer priority)
    {
        this.priority = priority;
    }
    
    public Time getTime()
    {
        return this.time;
    }
    
    public void setTime(Time time)
    {
        this.time = time;
    }
    
    public Date getDate()
    {
        return this.date;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public String getContent()
    {
        return this.content;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public Task(Integer id, Integer type, Integer priority, Date date, Time time, String content, Integer finish)
    {
        super();
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.time = time;
        this.date = date;
        this.content = content;
        this.finish = finish;
    }
    
    public Task()
    {
    }
}
