package sist.com.orm;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import sist.com.dao.BasicDao;
import sist.com.dto.MemberBean;
import sist.com.dto.OfficeBean;
import sist.com.dto.QnaBean;
import sist.com.dto.RentCarBean;
import sist.com.dto.RentListBean;

@Controller
@SessionAttributes(value = { "carList", "date1", "date2", "people", "car", "place" })
public class AppleController {
	@Autowired
	private BasicDao dao;
	/*
	 * @Resource private RentCarBean rentCarBean;
	 */

	// ����
	@RequestMapping(value = "carseach.do")
	public String carSeach(HashMap<String, String> map, HttpServletRequest request, Model model, HttpSession session) {
		map.put("car", request.getParameter("car"));
		map.put("place", request.getParameter("place"));
		model.addAttribute("carList", dao.selectRentCar(map));
		model.addAttribute("date1", request.getParameter("date1"));
		model.addAttribute("date2", request.getParameter("date2"));
		model.addAttribute("car", request.getParameter("car"));
		model.addAttribute("place", request.getParameter("place"));
		model.addAttribute("people", request.getParameter("people"));
		return "SIST/sub/reserve2";
	}

	@RequestMapping(value = "statemod.do")
	public String carState(int no, Model model, HttpServletRequest request, HttpSession session, RentListBean bean,String userId) {
		model.addAttribute("no", request.getParameter("no"));
		
		dao.updateState(no);
		bean.setRentdate((String) session.getAttribute("date1"));
		bean.setReturndate((String) session.getAttribute("date2"));
		bean.setCarno(Integer.parseInt(request.getParameter("no")));
		bean.setMemberno(dao.findmemno((String) session.getAttribute("userId")));
		bean.setPsgnum(Integer.parseInt((String) session.getAttribute("people")));
		bean.setOfcno(dao.findOfcno(no));
		bean.setRcprice(dao.findRcprice(no));
		dao.insertRentList(bean);
		return "redirect:carseach.do";
	}
	
	@RequestMapping(value="rentList.do")
	public String rentconfirm(Model model) {
		model.addAttribute("rentListView", dao.selectRentList());
		return "SIST/admin/rentmanage";
	}
	// ����
	
	// ȸ������
	@RequestMapping(value="loginPro.do")
	public String loginProcess(String userId, String userpassword,HttpSession session,Model model) {//���̵� ��й�ȣ�� ������ ��ġ�ϴ��� Ȯ��
			
			if(dao.memberIdCheck(userId,userpassword)) {
				session.setAttribute("userId", userId);
				return "redirect:loginCheck.do";
			}else {
				 
				/*model.addAttribute("loginFail","������ ��ġ���� �ʽ��ϴ�");*/
				
				return "redirect:/SIST/sub/login.jsp";
			}
	} 
	 
	@RequestMapping(value="loginCheck.do")
	public String loginChecksession(String userId,HttpSession session,Model model) {//���� Ȯ�� �� ������,ȸ�� ����
	 
		MemberBean bean = dao.findlevel((String) session.getAttribute("userId"));
		if(Integer.parseInt(bean.getMlevel())==0) {
			 return "/SIST/adminIndex";
			 
		}else if(Integer.parseInt(bean.getMlevel())==1) {
			 return "/SIST/index";
			 
		}
		return null;			
	}
	 
	@RequestMapping(value="idCheck.do")
	public String idCheckPro(HttpSession session, String memid) {
 
		System.out.println("IDIDIDIDID~~~~~~: " + memid);
		boolean check = dao.IdCheck(memid);
		 System.out.println("CHECK"+check);
	    return  "check";
		 
	}
	
	 @RequestMapping(value="logoutPro.do")
	 public String logoutPro(HttpServletRequest request) {//�α׾ƿ� = �α��� �� ������ ���� ����
		
		 HttpSession session = request.getSession();
		 session.invalidate();
		 return "/SIST/index";
	 }
	
	@RequestMapping(value="indexlogin.do")
	public String login() {
		return "redirect:/SIST/sub/login.jsp";
	}
	
	@RequestMapping(value="joinRentcar.do")
	public String joinmember() {
		
		return "redirect:/SIST/sub/joinRentcar.jsp";
	}
	
	
	@RequestMapping(value="memberInsert.do")
	public String insertMember(MemberBean bean,HttpSession session,Model model,String userId,ModelAndView mav) {
 
		bean.setMlevel("1");
		dao.insertMember(bean);
		model.addAttribute("userId",bean.getMemid());
		 
		return "SIST/sub/memberSuccess";
	}
	
	@RequestMapping(value="searchAdress.do")
	public String serarchAdress(String dong,Model model) {
		/* List<ZipBean>list=dong!=null?MemberDao.selectZipCode(dong):null;*/
		//dao.selectZipCode(dong);
 	model.addAttribute("dongList", dao.selectZipCode(dong)); 
		return "SIST/sub/address";
	}
	
	@RequestMapping(value = "myPage.do")
	public String goMypage(HttpServletRequest request, Model model, MemberBean bean) {
		HttpSession session = request.getSession();

		String idsession = session.getAttribute("userId").toString();
		MemberBean memInfo = new MemberBean();
		memInfo = dao.memberInfo(idsession);

		model.addAttribute("mypageId", memInfo.getMemid());
		model.addAttribute("mypagePw", memInfo.getMempw());
		model.addAttribute("mypageName", memInfo.getMname());
		model.addAttribute("mypageMail", memInfo.getMemail());

		String data = memInfo.getMadress();
		String[] adress = data.split("/");
		String adress1 = adress[0];
		String adress2 = adress[1];
		String adress3 = adress[2];
		model.addAttribute("mypageAdress1", adress1);
		model.addAttribute("mypageAdress2", adress2);
		model.addAttribute("mypageAdress3", adress3);

		model.addAttribute("mypageTel",memInfo.getMemtel());
 
		return "SIST/sub/myPage";

	}
	
	@RequestMapping(value="myPageReserve.do")
	public String goMyPageReserve(HttpSession session,Model model,String userId) {
	MemberBean mem = dao.findNo((String) session.getAttribute("userId"));
		List<RentListBean> list=dao.myPage_reserve(mem);
		model.addAttribute("myPageReserveList",list);
		
		return "SIST/sub/myPage_Reserve";
	}
	
	@RequestMapping(value="myQna.do")
	public String myQnaPro(Model model,HttpSession session,String userId) {
		MemberBean mem = dao.findNo((String) session.getAttribute("userId"));
		List<QnaBean> list = dao.myQnAselect(mem);
		model.addAttribute("myQnaList",list);
		
		return "SIST/sub/myQnA";
	}
	
	@RequestMapping(value="memberList.do")
	public String memberListPro(Model model) {
		List<MemberBean> list = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		list = dao.selectmember(map);
		model.addAttribute("selectMember",list);
		return "SIST/admin/member";
	}
	
 
	
	@RequestMapping(value="memberListDelete.do")
	public String memberListDelPro(int memno) {
		dao.deleteMemberList(memno);
		return "redirect:memberList.do";
	}
	
	@RequestMapping(value="goOffice.do")
	public String rentCarOffice(Model model) {
		List<OfficeBean> list = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		dao.selectOffice(map);
		
		list= dao.selectOffice(map);
		model.addAttribute("officeList",list);
		
		return "/SIST/admin/office";
	}
	//ȸ������
}
