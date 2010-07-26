
/**
 * Copyright 2009 Core Information Solutions LLC
 *
 * This file is part of Core Service Framework.
 *
 * Core Service Framework is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * Core Service Framework is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Core Service Framework.  If not, see 
 * <http://www.gnu.org/licenses/>.
 */package core.service.rule;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;

import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;

public class RuleExecutor
{
    /** logger for this class */
    public static Logger logger = LogFactory.getLogger(RuleExecutor.class);

    /** knowledge agent, lazy loaded */
    private volatile static KnowledgeAgent agent = null;

    /** knowledge base, lazy loaded */
    private volatile static KnowledgeBase base = null;
    
    /**
     * 
     */
    public RuleExecutor()
    {
        super();
    }

    /**
     * @param resources
     */
    public static void addResources(String[] resources)
    {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (int index = 0; index < resources.length; index++)
        {
            builder.add(ResourceFactory.newClassPathResource(resources[index], RuleExecutor.class), ResourceType.DRL);
        }
        if (builder.hasErrors())
        {
            logger.fatal("Failed to build rules: {0}.", builder.getErrors().toString());
        }
        else
        {
            getBase().addKnowledgePackages(builder.getKnowledgePackages());
        }
    }

    /**
     * Invoke rules on objects
     * 
     * @param objects
     */
    public void execute(List<Object> objects)
    {
        StatelessKnowledgeSession session = getBase().newStatelessKnowledgeSession();
        session.execute(objects);
    }
    
    /**
     * @return
     */
    public static KnowledgeAgent getAgent()
    {
        if (agent == null)
        {
            synchronized (RuleExecutor.class)
            {
                if (agent == null)
                {
                    agent = KnowledgeAgentFactory.newKnowledgeAgent("Service Agent");
                }
            }
        }
        return agent;
    }
    
    /**
     * @return
     */
    public static KnowledgeBase getBase()
    {
        if (base == null)
        {
            synchronized (RuleExecutor.class)
            {
                if (base == null)
                {
                    base = getAgent().getKnowledgeBase();
                }
            }
        }
        return base;
    }
}
