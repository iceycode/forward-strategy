package com.fs.game.unused_old_classes;

/** Contains unused methods from various classes
 *  methods are commented out
 * Created by Allen on 11/6/14.
 *
 * @deprecated
 *
 *
 */
public class OldMethods {

//
//
//	/** finds the other units on the stage
//	 *
//	 * @param stageUnits
//	 * @return
//	 */
//	public static Array<Unit> findOtherUnits(Array<Actor> stageUnits, Unit unit){
//		Array<Unit> findOtherUnits = new Array<Unit>();
//
//		for (Actor a : stageUnits) {
//			if (a.getClass().equals(Unit.class)) {
//				Unit uni = (Unit)a;
//				findOtherUnits.add(uni);
//			}
//		}
//
//		findOtherUnits.removeValue(unit, false);
//
//		return findOtherUnits;
//	}
//
//
//        public static ChangeListener unitImageChangeListener = new ChangeListener(){
//
//            @Override
//            public void changed(ChangeEvent event, Actor actor){
//                UnitImage currUnit = ((UnitImage)actor);
//                Array<String> unitDetailText = MenuUtils.UnitMenu.updateUnitText(currUnit.unitInfo);
//
//                if (currUnit.selected){
//                    Label unitDetail = UIUtils.createLabelInfo();
//                    Label unitDamageList = UIUtils.createLabelDamage();
//                    unitDetail.setText(unitDetailText.get(0));
//                    unitDamageList.setText(unitDetailText.get(1));
//
//                    MenuUtils.UnitMenu.showUnitInfo(unitDetail, unitDamageList, currUnit.getStage());
//                }
//            }
//
////            @Override
////            public boolean handle(Event event){
////
////                UnitImage currUnit = (UnitImage)event.getTarget();
////                Array<String> unitDetailText = MenuUtils.UnitMenu.updateUnitText(currUnit.unitInfo);
////
////                if (currUnit.selected){
////                    Label unitDetail = UIUtils.createLabelInfo();
////                    Label unitDamageList = UIUtils.createLabelDamage();
////                    unitDetail.setText(unitDetailText.get(0));
////                    unitDamageList.setText(unitDetailText.get(1));
////
////                    MenuUtils.UnitMenu.showUnitInfo(unitDetail, unitDamageList, currUnit.getStage());
////                }
////
////                return true;
////            }
//        };
//
    //FROM old unitscreen
    //	/** creates the listeners for units, panel & infopanel
//	 *
//	 */
//	public ChangeListener createChangeListener() {
//
//		ChangeListener unitChangeListener = new ChangeListener() {
//			private int clickCount = 0;
//
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				int id = Integer.parseInt(event.getTarget().toString());
//
//				clickCount++;
//
//				Gdx.app.log(LOG, " clicked unit " + "unit & button id: " + id +
//						", clickcount : " + clickCount);
//
//				//show dialogs based on click count
//				if (clickCount == 1) {
//					Dialog dia = createDialogBox(1);
//					dia.show(stage);
//				}
//
//
//				if (clickCount == 2 && choseUnit == true) {
//					Dialog dia = createDialogBox(2);
//					dia.show(stage);
// 				}
//
//				//gets the unit id from image buttons name
//				if (choseUnit == true) {
//					if (!unitForPlayer.contains(id, true)) {
//						unitForPlayer.add(id);
//						this.clickCount = 0;
//					}//only adds if not already there
//				}
//
//				if (choseUnit == false) {
//					if (unitForPlayer.contains(id, true)) {
//						unitForPlayer.removeValue(id, true);
//						clickCount = 0;
//					}//only adds if not already there
//				}
//
//				Gdx.app.log(LOG, "Current size of array: " + unitForPlayer.size);
//			}
//		};
//
//		return unitChangeListener;
//	}


    //
//	/** testSetup1
//	 * Humans vs Reptoids
//	 *
//	 */
//	public static void testBoardSetup1_12x12() {
// 		String faction1 = "Human";
//		String faction2 = "Reptoid";
//		String faction3 = "Arthroid";
//
//		Array<Unit> humanUni = UnitUtils.setUniPositions(faction1, 0, false, 1);
//		Array<Unit> reptoidUni = UnitUtils.setUniPositions(faction3, 11, true, 2);
//
//		p1Units.add(humanUni);
//		p1Units.add(reptoidUni);
//
//	}
//

    /** returns all possible moves
     * - gets the offset based on the max moves & unit position
     *
     * @param uni
     * @param panelPos
     */
//    public static Array<Panel> getMoveRange(Unit uni, Panel[][] panelPos) {
//        int maxMoves = uni.getMaxMoves(); //need to get from unit (constructor adjusts for larger units)
//        int gridPosX = uni.gridPosX;
//        int gridPosY = uni.gridPosY;
//
//        Array<Panel> panelArray = new Array<Panel>();
//        int[][] offsets;
//
//        //offset#, where number is max moves
//        if (maxMoves == 2) {
//            int[][] offsets2 = {
//                    {gridPosX-maxMoves, gridPosY},
//                    {gridPosX+maxMoves, gridPosY},
//                    {gridPosX+1, gridPosY},
//                    {gridPosX+1, gridPosY+1},
//                    {gridPosX+1, gridPosY-1},
//                    {gridPosX-1, gridPosY},
//                    {gridPosX-1, gridPosY+1},
//                    {gridPosX-1, gridPosY-1},
//                    {gridPosX, gridPosY+maxMoves},
//                    {gridPosX, gridPosY+1},
//                    {gridPosX, gridPosY-1},
//                    {gridPosX, gridPosY-maxMoves}
//            };
//            offsets = offsets2;
//
//        }
//        else if (maxMoves == 3) {
//            int[][] offsets3 = {
//                    {gridPosX-maxMoves, gridPosY},
//                    {gridPosX-1, gridPosY},
//                    {gridPosX-1, gridPosY+1},
//                    {gridPosX-1, gridPosY-1},
//                    {gridPosX-1, gridPosY+2},
//                    {gridPosX-1, gridPosY-2},
//                    {gridPosX-2, gridPosY},
//                    {gridPosX-2, gridPosY+1},
//                    {gridPosX-2, gridPosY-1},
//
//                    {gridPosX+1, gridPosY},
//                    {gridPosX+1, gridPosY+1},
//                    {gridPosX+1, gridPosY-1},
//                    {gridPosX+1, gridPosY+2},
//                    {gridPosX+1, gridPosY-2},
//                    {gridPosX+2, gridPosY},
//                    {gridPosX+2, gridPosY+1},
//                    {gridPosX+2, gridPosY-1},
//
//                    {gridPosX, gridPosY+maxMoves},
//                    {gridPosX, gridPosY+2},
//                    {gridPosX, gridPosY+1},
//                    {gridPosX, gridPosY-1},
//                    {gridPosX, gridPosY-2},
//                    {gridPosX+maxMoves, gridPosY}
//
//            };//gets all offsets for 3 moves
//            offsets = offsets3;
//        }
//        else if (maxMoves == 4) {
//            int[][] offsets4 = {
//                    {gridPosX-maxMoves, gridPosY},
//                    {gridPosX+maxMoves, gridPosY},
//
//                    {gridPosX+1, gridPosY},
//                    {gridPosX+1, gridPosY+1},
//                    {gridPosX+1, gridPosY-1},
//                    {gridPosX+1, gridPosY+2},
//                    {gridPosX+1, gridPosY-2},
//                    {gridPosX+1, gridPosY+3},
//                    {gridPosX+1, gridPosY-3},
//
//                    {gridPosX+2, gridPosY},
//                    {gridPosX+2, gridPosY+1},
//                    {gridPosX+2, gridPosY-1},
//                    {gridPosX+2, gridPosY+2},
//                    {gridPosX+2, gridPosY-2},
//
//                    {gridPosX-2, gridPosY},
//                    {gridPosX-2, gridPosY+1},
//                    {gridPosX-2, gridPosY-1},
//                    {gridPosX-2, gridPosY+2},
//                    {gridPosX-2, gridPosY-2},
//
//                    {gridPosX+3, gridPosY},
//                    {gridPosX+3, gridPosY+1},
//                    {gridPosX+3, gridPosY-1},
//
//                    {gridPosX-3, gridPosY},
//                    {gridPosX-3, gridPosY+1},
//                    {gridPosX-3, gridPosY-1},
//
//                    {gridPosX-1, gridPosY},
//                    {gridPosX-1, gridPosY+1},
//                    {gridPosX-1, gridPosY-1},
//                    {gridPosX-1, gridPosY+2},
//                    {gridPosX-1, gridPosY-2},
//                    {gridPosX-1, gridPosY+3},
//                    {gridPosX-1, gridPosY-3},
//
//                    {gridPosX, gridPosY+maxMoves},
//                    {gridPosX, gridPosY+3},
//                    {gridPosX, gridPosY+2},
//                    {gridPosX, gridPosY+1},
//                    {gridPosX, gridPosY-1},
//                    {gridPosX, gridPosY-2},
//                    {gridPosX, gridPosY-3},
//                    {gridPosX, gridPosY-maxMoves}
//            };//gets all offsets for 3 moves
//            offsets = offsets4;
//        }
//        else {
//            int[][] offsets1 = {
//                    {gridPosX-maxMoves, gridPosY},
//                    {gridPosX+maxMoves, gridPosY},
//                    {gridPosX, gridPosY+maxMoves},
//                    {gridPosX, gridPosY-maxMoves}
//            };
//            offsets = offsets1;
//        }
//
//        //places correct panels based on offset into array
//        for (int[] o : offsets) {
//            //makes sure units are not out of bounds
//            if ((o[0] >= 0 && o[1] >= 0) &&
//                    (o[0]<= 11 && o[1] <= 11)) {
//                panelArray.add(panelPos[o[0]][o[1]]);
//            }
//        }
//
//        return panelArray;
//    }

/*  *//** creates a camera for the map
     *//*
public static void createMapCam() {
	*//*****camera for tiled map*****//*
	float width = 12 * (screenWidth/screenHeight); //width aspect ratio corrected for
	float height = 12; //the height, fills to top

	camera = new OrthographicCamera();
	camera.setToOrtho(false, 800, 500 ); //sets scale of units for rendered

	//set screen viewport
	viewport = new ScreenViewport();
	viewport.setWorldWidth(screenWidth);
	viewport.setWorldHeight(screenHeight);
	viewport.setCamera(camera);

    mapCam = new OrthographicCamera(width, height);
		mapCam.setToOrtho(false, 800, 500);

	vecPos = new Vector3(gridOriX, gridOriY, 1);
	camera.project(vecPos); //project the stage camera
	mapCam.unproject(vecPos); //unproject to return position of map camera

	vecPos.x = gridOriX;
	vecPos.y = gridOriY;

	mapCam.position.set(vecPos);
	camera.position.set(gridOriX, gridOriY, 0);

	viewport.setCamera(camera);

	setViewport(viewport);//sets this stage viewport

}




//*************TABLE GRID******w************
makes a Table which can be added to stage
	public static Table createPanelTable(Panel[][] panelMatrix) {
		Table table = new Table();
		table.setFillParent(false);

		for(int x = 0; x < Constants.ROWS; x++)  {
			for (int y = 0; y < Constants.COLS; y++ ) {
				// final Panel p = new Panel(stage, tiles, x, y);
				Panel panelActor = panelMatrix[x][y];
				//panelActor.addListener(MapUtils.createPanelListener(panelActor));
				table.add(panelActor).width(panelActor.getWidth()).height(panelActor.getHeight());
				table.addActor(panelActor);
			}
			table.row(); //creates a row out of the actors
		}

		return table;
 	}
*
*
*
*
*
*/



    /** checks to see if neighbor left or right
     * - doubly links together parent & child

    public static boolean vertNeighbor(Panel parent, Panel child){
    //return Math.abs(n1.y-n2.y)==maxY && n1.x==n2.x;
    // 		Gdx.app.log(LOG, "parent is at : " + "(" + parent.getX() + ", " + parent.getY() +")");
    // 		Gdx.app.log(LOG, "child is at : " + "(" + child.getX() + ", " + child.getY() +")");

    boolean verticalNeighbor = false;

    if ((parent.getY() == child.getY() + 32)  && parent.getX()==child.getX() ) {
    child.panelAbove = parent;
    parent.panelBelow = child;
    parent.neighbor = child;
    child.neighbor = parent;

    verticalNeighbor = true;
    }
    else if ((parent.getY() == child.getY() - 32) && parent.getX()==child.getX() ){
    parent.panelAbove = child;
    child.panelBelow = parent;
    parent.neighbor = child;
    child.neighbor = parent;

    verticalNeighbor = true;
    }


    return verticalNeighbor;
    }

    public static boolean horizNeighbor(Panel parent, Panel child){
    boolean horizNeighbor = false;

    if ((parent.getX() == child.getX() + 32) && parent.getY()==child.getY()){
    child.panelLeft = parent;
    parent.panelRight = child;
    parent.neighbor = child;
    child.neighbor = parent;

    horizNeighbor = true;

    }
    else if ((parent.getX() == child.getX() - 32) && parent.getY()==child.getY()){
    child.panelRight = parent;
    parent.panelLeft = child;
    child.neighbor = parent;
    parent.neighbor = child;

    horizNeighbor = true;
    }


    return horizNeighbor;
    }
     */

//
//    public static Table tableFromLayers(TiledMapTileLayer tiledLayer) {
//        Table layerTable = new Table();
//        layerTable.setFillParent(true);
//
//        for (int x = 0; x < tiledLayer.getWidth(); x++) {
//            for (int y = 0; y < tiledLayer.getHeight(); y++) {
//                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
//
//                MapActor mapActor = new MapActor(tiledMap, tiledLayer, cell);
//
//                mapActor.setBounds(x * tiledLayer.getTileWidth(), y * tiledLayer.getTileHeight(), tiledLayer.getTileWidth(),
//                        tiledLayer.getTileHeight());
//                mapActor.setPosition(GRID_ORI_X+x*mapActor.getWidth(), GRID_ORI_Y+y*mapActor.getHeight());
//                mapActor.addListener(new ClickListener() {
//                    @Override
//                    public void clicked(InputEvent event, float x, float y) {
//                        Gdx.app.log("log: ", " tilemap actor clicked at " + x + y);
//                    }
//                });
//
//                //add to table
//                layerTable.add(mapActor).width(32).height(32);
//                layerTable.addActor(mapActor);
//            }//get all the columns
//
//            layerTable.row();
//        }//get all the rows
//
//        return layerTable;
//    }
//
//
//    /** creates actors from the TiledMapTileSet
//     *
//     * @param tileset
//     */
//    public static void createActorsFromTileSets(TiledMapTileSet tileset, MapStage stage) {
//        for (TiledMapTile tile : tileset) {
//            Object property = tile.getProperties().get("Water");
//
//            if (property != null) {
//                MapActor mapActor = new MapActor(tile, GRID_ORI_X, GRID_ORI_X);
//                stage.addActor(mapActor);
//            }
//        }
//    }
/*--------------------Grid Panels on Board----------------
 * methods to create grid panels which show unit moves
 *
 *
 */
    /*****Sets all the panels positions & actors in matrix
     * - sets all game board actors as arrays
     */
//    public static void setupPanels12x12() {
//
//        int rows = 12;
//        int columns = 12;
//        float width = Constants.GRID_TILE_WIDTH;
//        float height = Constants.GRID_TILE_HEIGHT;
//
//        Array<Panel> panelsOnStage = new Array<Panel>(rows*columns); //<----not using now
//        Panel[][] panelMatrix = new Panel[rows][columns];
//
//
//        for (int x = 0; x < rows; x ++) 	{
//            String panelName = "x"+x;
//            if (x%2==0){
//                System.out.println();
//            }
//            for (int y = 0; y < columns; y++) 	 {
//                float stagePosX = x*width + GRID_ORI_X;
//                float stagePosY = y*height + GRID_ORI_Y;
//
//                //String gridPos = "{" + x + ", " + y + "}, ";
//                String screenPos = "[" + stagePosX + ", " + stagePosY+ "], ";
//                System.out.print(screenPos);
//                //System.out.print(gridPos);
//
//
//                Panel panelActor = new Panel(stagePosX, stagePosY);
//                panelActor.setName(panelName.concat("y"+y)); //used for id
//                panelActor.setMatrixPosX(x);
//                panelActor.setMatrixPosY(y);
//
//                //panelActor.toFront();
//                panelMatrix[x][y] = panelActor; //store in position matrix
//                panelsOnStage.add(panelActor);
//            }
//        }
//
//        //setup the game board = stored in constants for pathfinding
//
//
//        //set the elements which will be used on stage & by Unit actors
//        GameData.gamePanels = panelsOnStage;
//        GameData.panelMatrix = panelMatrix;
//    }

}
