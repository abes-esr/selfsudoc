var liste955= new Array(100);
        var liste955Key= new Array(100);
        var ii=0;
        var sortie="";
        var c="";var a="";var b="";var i="";var j="";var k="";var z="";var w="";
        
                function AffLesZones()
                {
                
                
                if (c!="")
                          {sortie=sortie+ " sér."+ c ;}
                if (a!="")
                          {sortie=sortie+ " vol."+ a ;}
                if (b!="")
                          {sortie=sortie+ " n°"+ b ;}
                if ((k!="") || (j!="") || (i!=""))
                          {sortie=sortie+ "("+ k+j+i+")" ;}
                }


        function Traite955Init()
        {
                ii=0;
                return "";
        }

        function Traite955Add(key,val)
        {

                liste955[ii]=val;
                liste955Key[ii]=key;
                ii=ii+1;
        return "";
        }
        function Traite955()
        {
                var jj=0;
                sortie=""; c=""; a=""; b=""; i=""; j=""; k=""; z=""; w="";
                for (jj=0;jj<ii;jj++)
                {
                
                if (liste955Key[jj]=="c")
                   {
                    if ((c!="")|| (i!="") || (j!="") || (k!=""))
                        {
                        AffLesZones();
                        sortie=sortie+"-";      
                        c=liste955[jj];a="";b="";i="";j="";k="";
                        }
                    else
                        {
                        c=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="a")
                   {
                    if ((a!="")|| (i!="") || (j!="") || (k!=""))
                        {
                        AffLesZones();
                        sortie=sortie+"-";      
                        c="";a=liste955[jj];b="";i="";j="";k="";
                        }
                    else
                        {
                        a=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="b")
                   {
                    if ((b!="")|| (i!="") || (j!="") || (k!=""))
                        {
                        AffLesZones();
                        sortie=sortie+" - ";    
                        c="";b=liste955[jj];a="";i="";j="";k="";
                        }
                    else
                        {
                        b=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="i")
                   {
                    if (i!="")
                        {
                        AffLesZones();
                        sortie=sortie+" - ";    
                        c="";i=liste955[jj];b="";a="";j="";k="";
                        }
                    else
                        {
                        i=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="j")
                   {
                    if (j!="")
                        {
                        AffLesZones();
                        sortie=sortie+" - ";    
                        c="";j=liste955[jj];b="";i="";a="";k="";
                        }
                    else
                        {
                        j=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="k")
                   {
                    if (k!="")
                        {
                        AffLesZones();
                        sortie=sortie+" - ";    
                        c="";k=liste955[jj];b="";i="";j="";a="";
                        }
                    else
                        {
                        k=liste955[jj];
                        }
                   }
                else
                if (liste955Key[jj]=="g")
                   {
                    AffLesZones();
                        sortie=sortie+" = ";    
                        c="";k="";b="";i="";j="";a="";
                        
                   }
                else
                if (liste955Key[jj]=="w")
                   {
                        w=liste955[jj];
                   }
                else
                if (liste955Key[jj]=="z")
                   {
                        z=liste955[jj];
                   }
                }
                AffLesZones();
                if (z!="")
                        {sortie=sortie+" ["+z+"]";}
                if (w!="")
                        {sortie=sortie+" ["+w+"]";}

        return sortie;
        }
        

        function Traite955_Aff(val)
                {
                return liste955[val];
                }
        function Traite916(val)
        {

        	if (isNaN(val.substring(3,4)))
        	{
        		unit = val.substring(3,4);
        		value = val.substring(2,3);        			
        	}
        	else {
        		unit = val.substring(4,5);
        		value = val.substring(2,4);
        	}

        	if (unit=="s")
        	{return value + " semaine(s)" ;}
        	if (unit=="m")
        	{return value + " mois" ;}
        	if (unit=="a")
        	{return value + " année(s)" ;}
        	return "error:"+val;
        }

        function Traite955_AffK(val)
                {
                return liste955Key[val];
                }
        function Substring(param,debut,longueur)
                {
                return param.substring(debut-1,debut+longueur-1);
                }
