package com.github.abrarsyed.gmcp

class MCModInfo
{
    String modid
    String name
    String description
    def version
    def mcversion
    String url = ""
    String updateUrl = ""
    def authors = []
    String credits = ""
    def logofile = ""
    def screenshots
    def dependancies
    
    public String asJSON()
    {
        // TODO: MAKE JSON
    }
    
}


/*
[
{
  "modid": "secretroomsmod",
  "name": "The SecretRoomsMod",
  "description": "This mod adds a variety of cool blocks that camouflage themselves to the surrounding world. All of these blocks have extremely useful functions that allow you to simply hide your diamonds, or punish all who dare try to get to them. Hidden doors, pressure plates, hidden levers and more!",
  "version": "4.6.1",
  "mcversion": "1.5",
  "url": "http://www.minecraftforum.net/topic/546192-",
  "updateUrl": "http://files.minecraftforge.net/SecretRoomsMod/SecretRoomsMod-universal-latest.zip",
  "authors": ["AbrarSyed", "alexbegt"],
  "credits": "Authored by AbrarSyed, based on an original idea by iampeppino. Help provided by alexbegt.",
  "logoFile": "",
  "screenshots": [],
  "dependencies": []
}
]
*/