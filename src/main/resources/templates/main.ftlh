<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html" xmlns="http://www.w3.org/1999/html">
<head>
    <title>Simple Task Tracker System</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<h1>Now its a main page</h1>
<form action="/addUser" method="post">
    Username: <input type="text" name="username" placeholder="Enter user's name"/>
    <button type="submit">Add user</button>
</form>
<a href="/showAllUsers">Show All Users</a></br>
<#if showUsers>
    <#list users as user>
        ${user.getUsername()} :
        <a href="/showUserTasks/${user.id}">Show ${user.getUsername()}'s tasks</a>
            <#list showUserTasks as showUser>
             <#if showUser.id==user.id>
                <#list user.getTasks() as task>
                  Task ID: ${task.id} , Aim: ${task.getTaskAim()}
                    <#if !task.isDone()&&!task.parentTask??>
                        , Remaining Time: days:hours:miniutes:seconds - ${task.getRemainingTime(currentTime)}
                    <#elseif task.isDone()>
                        DONE!
                    </#if>
                </#list>

             </#if>
            </#list>
        <a href="/showUserProjects/${user.id}">Show ${user.username}'s projects</a>
        <#list showUserProjects as showuser>
            <#if showuser.id==user.id>
                <#list user.getProjects() as project>
                    Project ID: ${project.id} , Aim: ${project.getProjectAim()}
                    <a href="/generateReport/${project.id}/${user.id}">Generate Report for All Tasks of Specified user</a>
                </#list>
            </#if>
        </#list>
        <a href="/deleteUser/${user.id}">Delete User</a></br>
    </#list>
</#if>
<a href="/showAllProjects">Show All Projects</a></br>
<#if showProjects>
    <#list projects as project>
        Project Id: ${project.id} , Aim: ${project.projectAim}</br>
         <#list project.getTasks() as task>
             Task ID: ${task.id} , Aim: ${task.getTaskAim()}
         </#list></br>
        <a href="/deleteProject/${project.id}">Delete Project</a>
    </#list>
</#if>
<form action="/addProject" method="post" id="addProject">
    Project Aim: <input type="text" name="projectAim" placeholder="Enter project aim"/>
</form>
<form action="/assignUsersToProject" method="post">
    Assign The Project to:
    <#if users??>
        <#list users as user>
            <input type="radio" name="${user.username}" value="${user.id}">${user.username}</input>
        </#list>
    </#if>
    <button type="submit">Assign Users</button>
</form>
<#list assignedUsers as user>
    <a href="/unsignUser/${user.id}">Unsign ${user.username}</a></br>
</#list>
<a href="/addNewTask">Add Task</a>
<#list tasks as task>
    <form action="/addTask" method="post" id="${task.id}addTask">
        Task Aim: <input type="text" name="taskAimTemp" <#if task.taskAim??>value="${task.taskAim}"</#if>placeholder="Enter tesk aim"/>
        Estimation time (hours) : <input type="text" name="estimationTime" <#if task.estimationTime??>value="${task.performEstimationTime()}"</#if>placeholder="Estimation time"/>
        <#if !task.parentTask??>
            Assign The Task to:
            <#list assignedUsers as user>
                <input type="radio" name="singleUser" value="${user.username}" <#if task.getUser()??&&task.getUser().getId()==user.getId()>checked</#if>>${user.getUsername()}</input>
            </#list>
        </#if>
        <input type="hidden"  name="task" value="${task.id}" />
    </form>
    <a href="/addSubTask/${task.id}">Add Subtask</a>
    <#if task.getSubTasks()??>
        <#list task.getSubTasks() as subTask>
            Id: ${subTask.id}, SubTask Aim: <#if subTask.taskAim??>${subTask.taskAim}</#if>
        </#list>
    </#if></br>
    <button type="submit" form="${task.id}addTask">Add task</button>
    <a href="/deleteTask/${task.id}">Delete Task</a>
</#list></br>
<button type="submit" form="addProject">Add Project</button>
</body>
</html>